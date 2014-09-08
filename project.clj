(defproject dartbot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [http-kit "2.1.18"]
                 [compojure "1.1.8"]
                 [ring "1.3.0"]
                 [org.clojure/tools.nrepl "0.2.4"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [cheshire "5.3.1"]
                 [liberator "0.12.0"]
                 [org.clojure/tools.cli "0.3.1"]]
  :main dartbot.core
  :aot :all)
