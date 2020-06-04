(ns game.cards
  (:require [arcadia.core :refer :all]
            [game.utils :as utils]
            [game.mouse :as mouse]
            [arcadia.linear :refer :all])
  (:use arcadia.core)
  (:import [UnityEngine UI.Text UI.Image Vector3 Transform]))

(def active-card (atom nil))

(def canvas (object-named "Canvas"))

(def deck-position (v3 10 10 0))
(def snapback-position (atom nil))

(def all-cards [{:name "Examine"
                 :count 4
                 :cost 1
                 :learning 5}
                {:name "Block"
                 :cost 1
                 :count 4
                 :block 5}
                {:name "Attack"
                 :cost 1
                 :count 6
                 :power 5}])

(defn snapback [obj role-key]
  (reset! active-card nil)
  (set! (. (cmpt obj Transform) position) @snapback-position))

(defn card-clicked [obj role-key]
  (reset! active-card obj)
  (reset! snapback-position (. (cmpt obj Transform) position)))

(defn follow-mouse [obj role-key]
  (when (= obj @active-card)
   (mouse/follow-mouse obj)))

(defn expand-deck []
  (mapv (fn [{:keys [name cost learning block power] :as x}]
         (let [card (instantiate (UnityEngine.Resources/Load "Card") (v3 10 10 0))
                    [_ _ c n t i] (descendents card)]
              (set! (.text (cmpt c Text)) (str (:cost x)))
              (set! (.text (cmpt n Text)) (str (:name x)))
              (set! (.text (cmpt t Text)) (str
                                           (when learning (str "Learning: " learning))
                                           (when block (str "Block: " block))
                                           (when power (str "Power: " power))))
              (set! (.sprite (cmpt i Image)) (last (UnityEngine.Resources/LoadAll (str "Cards/" name))))
              (child+ canvas card)
              (hook+
                 card
                 :on-mouse-down
                 :click
                 #'card-clicked)
              (hook+
                 card
                 :update
                 :follow-mouse
                 #'follow-mouse)
              (hook+
                 card
                 :on-mouse-up
                 :release-mouse
                 #'snapback)
              (assoc x :object card)))

       (mapcat (fn [{:keys [count name] :as x}] (repeat count x)) all-cards)))

(def deck (shuffle (expand-deck)))

(defn random-card []
  (let [index (rand-int (count deck))]
   (deck index)))

(expand-deck)

(defn draw-hand []
  (map-indexed (fn [i {:keys [object]}]
                (set! (. (cmpt object Transform) position) (v3 (* (- i 2) 2) -3 0)))
               (take 5 deck)))


(count (draw-hand))
