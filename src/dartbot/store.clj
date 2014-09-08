(ns dartbot.store
  (:import (java.io File))
  (:use clojure.java.io)
  (:use dartbot.game.301))

(def current-path "games/current/")
(def archive-path "games/archive/")
(make-parents (str current-path "file"))
(make-parents (str archive-path "file"))

;(defn read-file [file]
;  (read-string (slurp file)))

(defn load-dir [dir-path]
  (into {} (for [file (file-seq (file dir-path)) :when (.isFile file)]
                    [(.getName file) (load-file (.getPath file))])))

(defn list-dir [dir-path]
  (for [file (file-seq (file dir-path)) :when (.isFile file)]
    (.getName file)))

(defonce games ^{:private true} (atom (load-dir current-path)))
(defonce file-list ^{:private true} (atom (list-dir archive-path)))

(defn load-game [id]
  (read-string (slurp (str current-path id))))

(defn get-game [id]
  (if-let [game (get @games id)]
    game
    (if (.exists (file (str archive-path id))) ;(some #{id} @file-list)
      (read-string (slurp (str archive-path id))))
    ))

(defn store-game [id game]
  (swap! games assoc id game)
  (spit (str current-path id) (pr-str game)))

(defn delete-game [id]
  (swap! games dissoc id)
  (delete-file (str current-path id)))

(defn archive-game [id]
  (spit (str archive-path id) (pr-str (get-game id)))
  (delete-game id))

