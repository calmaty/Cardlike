(ns game.enemies
  (:require [arcadia.core :refer :all]
            [game.utils :as utils]
            [game.mouse :as mouse]
            [arcadia.linear :refer :all])
  (:use arcadia.core)
  (:import [UnityEngine UI.Text UI.Image UI.Slider Vector3 Transform]))

(def canvas (object-named "Canvas"))

(def active-enemy {})

(def all-enemies [{:name "imp"
                   :attack 15
                   :block 0
                   :health 30
                   :damage 0
                   :exp 15
                   :gained 0
                   :challenge 1
                   :text "a small insignificant imp"}])

(defn update-text [obj current total]
  (utils/change-text obj (str (current active-enemy) "/" (total active-enemy))))

(defn update-enemy [obj role-key]
  (let [[_ _ t n i l ab _ db _ lb _ at dt lt] (descendents obj)]
     (mapv update-text [at dt lt] [:damage :block :gained] [:health :attack :exp])
     (mapv utils/change-slider [ab db lb] (map active-enemy [:damage :block :gained]))))


(defn spawn-enemie [obj role-key]
  ((fn [{:keys [name attack health exp challenge text] :as x}]
      (let [object (instantiate (UnityEngine.Resources/Load "Encounter") (v3 0 1 0))
              [_ _ t n i l ab _ db _ lb _ at dt lt] (descendents object)]
           (mapv utils/change-text [l n t at dt lt] [challenge name text health attack exp])
           (set! (.sprite (cmpt i Image)) (last (UnityEngine.Resources/LoadAll (str "Enemies/" name))))
           (child+ canvas object)
           (mapv utils/set-slider-max [ab db lb] [health attack exp])
           (hook+
              object
              :update
              :update-enemy
              #'update-enemy)
           (assoc x :object object))) (first all-enemies)))

(def active-enemy (spawn-enemie 2 2))
