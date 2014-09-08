(ns dartbot.game.301
  (:use dartbot.protocol
        dartbot.helpers))

(defrecord G301 [data]
  Game
  (render [this] (render-page "game-head" data ["game-body"]))
  (render [this options] (case options
                           "lite" (render-page "game-body" data ["game-body"])
                           "full" (render this)
                           (str "Invalid option: " options)))
  (update [this commmand] this)
  )

(defn make-game [data]
  (->G301 data))
