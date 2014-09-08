(ns dartbot.protocol)

(defprotocol Game
  (render [this] [this options])
  (update [this command]))