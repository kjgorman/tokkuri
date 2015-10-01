(defproject tokkuri "0.1.0-SNAPSHOT"
  :description "A small exercise for circuit breakers/back offs"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [compojure "1.4.0"]
                 [com.taoensso/carmine "2.11.1"]
                 [pandect "0.5.4"]]
  :plugins [[lein-ring "0.8.1"]]
  :ring { :handler tokkuri.core/handler })
