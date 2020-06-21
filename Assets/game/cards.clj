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

(def deck (atom []))

(def hand (atom []))

(def discard (atom []))

(defn discard-card [object]
    (let [card (first (filter #(= object (:object %)) @hand))]
      (set! (. (cmpt object Transform) position) deck-position)
      (swap! hand (fn [x] (remove #(= card %) x)))
      (swap! discard conj card)
      (log "hand-" (count @hand))
      (log "discard-" (count @discard))))

(defn play-card-from-hand [obj] ;maybe get the entire card here and parse it to poth functions
 #_(play-card (first (filter #(= object (:object %)) @hand)))
 (discard-card obj))


(defn snapback [obj role-key]
  (reset! active-card nil)
  (if (< -1 (.. (cmpt obj Transform) position y))
    (play-card-from-hand obj)
    (set! (. (cmpt obj Transform) position) @snapback-position)))

(defn card-clicked [obj role-key]
  (reset! active-card obj)
  (reset! snapback-position (. (cmpt obj Transform) position))
  (. (cmpt obj Transform) SetAsLastSibling))

(defn follow-mouse [obj role-key]
  (when (= obj @active-card)
   (mouse/follow-mouse obj)))

(defn expand-deck []
  (mapv (fn [{:keys [name cost learning block power] :as x}]
         (let [card (instantiate (UnityEngine.Resources/Load "Card") (v3 10 10 0))
                    [_ _ c n t i] (descendents card)]
              (mapv utils/change-text [c n t] [cost name (str
                                                          (when learning (str "Learning: " learning))
                                                          (when block (str "Block: " block))
                                                          (when power (str "Power: " power)))])
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

(reset! deck (shuffle (expand-deck)))

(defn random-card []
  (let [index (rand-int (count @deck))]
   (deck @index)))

#_(expand-deck)

(defn draw-hand []
  (log (count @deck))
  (when (< (count @deck) 5)
     (do
       (swap! deck concat @discard)
       (reset! discard [])))
  (map-indexed (fn [i {:keys [object] :as card}]
                (swap! hand concat [card])
                (set! (. (cmpt object Transform) position) (v3 (* (- i 2) 2) -3 0))
                (swap! deck (fn [x]
                             (remove #(= % card) x))))
               (take 5 @deck)))

(defn discard-hand []
  (mapv (fn [{:keys [object] :as card}]
         (set! (. (cmpt object Transform) position) deck-position)) @hand)
  (swap! discard concat @hand)
  (reset! hand []))

(defn new-turn [obj role-key]
  (log "new turn")
  (discard-hand)
  (doall (draw-hand)))

(count (draw-hand))
#_(discard-hand)

(log (count @hand))

(log (count @discard))

(log (count @deck))
