(ns game.cards
  (:use arcadia.core)
  (:import [UnityEngine UI.Text UI.Image]))

(def canvas (object-named "Canvas"))

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

(defn expand-deck []
  (mapv (fn [{:keys [name cost learning block power] :as x}]
         (let [card (instantiate (UnityEngine.Resources/Load "Card"))
                    [_ _ c n t i] (descendents card)]
              (set! (.text (cmpt c Text)) (str (:cost x)))
              (set! (.text (cmpt n Text)) (str (:name x)))
              (set! (.text (cmpt t Text)) (str
                                           (when learning (str "Learning: " learning))
                                           (when block (str "Block: " block))
                                           (when power (str "Power: " power))))
              (set! (.sprite (cmpt i Image)) (last (UnityEngine.Resources/LoadAll (str "Cards/" name))))
              (child+ canvas card)
              (assoc x :object card)))
       (mapcat (fn [{:keys [count name] :as x}] (repeat count x)) all-cards)))

(def deck (expand-deck))

(defn random-card []
  (let [index (rand-int (count deck))]
   (deck index)))

(expand-deck)
