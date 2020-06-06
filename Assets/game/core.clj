(ns game.core
  (:require [arcadia.core :refer :all]
            [game.cards :as cards]))

(def end-button (object-named "End Button"))

(hook+
  end-button
  :on-mouse-down
  :end-turn
  #'cards/new-turn)

(log "e")
