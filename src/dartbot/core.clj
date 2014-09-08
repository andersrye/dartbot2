(ns dartbot.core
  (:require [org.httpkit.server :refer [run-server with-channel on-close on-receive send!]]
            [cheshire.core :refer [parse-string generate-string]]
            [compojure.route :refer [files not-found]]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes GET POST DELETE ANY context]]
            [liberator.core :refer [resource defresource]]
            [clojure.tools.nrepl.server :refer [start-server stop-server]]
            [ring.middleware.params :refer [wrap-params]]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.cli :refer [parse-opts]])
  (:use dartbot.helpers
        dartbot.store
        dartbot.protocol)
  (:import (java.io StringWriter)
           (org.apache.commons.io IOUtils)
           (java.text SimpleDateFormat))
  (:gen-class))

(declare routes ch game games-list game-id opt) ;not needed just to reduce annoying "cannot be resolved" spam
(defonce nrepl (start-server :port 4439))


(defn handler [req]

  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello world igjen!"})

(defn render-game [game-id opts]

  (fn [req]
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    (render (get-game game-id) opts)})
  )

(defn render-list [req]
  (render-page "test" {:current (for [[k v] @games]
                                            (assoc (:data v) :id k :date (.format (SimpleDateFormat. "MM.dd.yy") (* 1000 (:timestamp (:data v))))))
                                 :archive (for [[k v] (load-dir archive-path)]
                                            (assoc (:data v) :id k :date (.format (SimpleDateFormat. "MM.dd.yy") (* 1000 (:timestamp (:data v))))))})
   )

(defn parse-is [is]
  (parse-string
    (let [w (StringWriter.)]
      (IOUtils/copy is w)
      (.toString w))))

(defn make-new-game [req]
  (pprint (parse-is (:body req)))
  {:status  303
   :headers {"Location" "/game/qwe123/"}
   })


(defn send-forever [ch data]
  (future
    (loop []
      (Thread/sleep 2000)
      (println "sending!")
      (send! ch data)
      (recur))))

(defn ws-handler [req]
  (println "somebody's connecting!!")
  (with-channel req ch
                (on-receive ch (fn [data] (do (println data) (send! ch data))))
                (on-close ch (fn [status] (println status)))
                (send-forever ch "hei")
                ))

(defresource games-list
             :available-media-types ["text/html"]
             :handle-ok (fn [ctx] (render-list ctx))
             )

(defresource game [game-id]
             :available-media-types ["text/html"]
             :exists? (fn [_ctx]
                        (if-let [game (get-game game-id)]
                          {:game game}))
             :handle-ok (fn [ctx]
                          (render (:game ctx))))

(defroutes routes
           (ANY "/" [] (str "hei :)"))
           (GET "/ws" [] ws-handler)
           (ANY "/game" [] games-list)
           (ANY "/games" [] games-list)
           (context "/game/:game-id" [game-id]
                    (ANY "/" [] (render (get-game game-id)))
                    (GET "/:opt" [opt] (render-game game-id opt))
                    )
           (files "/" {:root "resources"})
           (not-found "Oops! (404)"))


(def cli-opts
  [["-p" "--port PORT" "Port number"
    :default 4440
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-s" "--serial ON/OFF" "Serial monitor on/off"
    :default true
    :parse-fn #((or (= (clojure.string/lower-case %) "on") (= (clojure.string/lower-case %) "true")))]
   ["-h" "--help" "For this help"]])

(defn -main [& args]
  (let [{:keys [options errors summary]} (parse-opts args cli-opts)
        {:keys [port serial help]} options]
    ;(prn port serial help)
    (when errors (println "ERROR:") (doseq [e errors] (println e)) (println))
    (if (or help errors)
      (do
        (println "Run with \"lein [trampoline] run [opts]\".")
        (println "The following options are available:")
        (println summary)
        (System/exit 0))
      (do
        (println "For options run \"lein run --help\"")
        (run-server (site (-> #'routes wrap-params)) {:port port})
        (println "Server started on port" port)))

    ))
