(ns game.mouse
  (:require [arcadia.core :refer :all])
  (:import [UnityEngine Vector3 Resources SpriteRenderer Color Input Camera]))

(defn follow-mouse [obj] ;could maybe be optimized
 (let [mouse-pos (. Input mousePosition)
       mouse-world-pos (. (.. Camera main) ScreenToWorldPoint (Vector3. (. mouse-pos x) (. mouse-pos y) 1))]
      (set! (.. obj transform position) mouse-world-pos)))
