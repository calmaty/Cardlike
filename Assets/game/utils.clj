(ns game.utils
  (:require [arcadia.core :refer :all])
  (:import [UnityEngine UI.Text UI.Slider UI.Image Vector3 Transform]))

(defn rat
     [x]
     (do (println x)
       (log x)
      x))

(defn change-text [obj text]
  (set! (.text (cmpt obj Text)) (str text)))

(defn change-slider [obj value]
  (set! (.value (cmpt obj Slider)) value))

(defn set-slider-max [obj max]
  (set! (.maxValue (cmpt obj Slider)) max))
