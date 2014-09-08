(ns dartbot.helpers
  (:require [clojure.java.io :as io])
  (:use [clostache.parser :only [render-resource]]))

(defn render-page
  ([template data]
   (render-resource (str "templates/" template ".mustache") data))
  ([template data partials]
   (render-resource
     (str "templates/" template ".mustache")
     data
     (reduce (fn [accum partial]
               (assoc accum (keyword partial) (slurp (io/resource (str "templates/" partial ".mustache")))))
             {} partials))))

