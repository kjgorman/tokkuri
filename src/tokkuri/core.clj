(ns tokkuri.core
  (:use [tokkuri.middleware])
  (:require [compojure.core  :refer :all]
            [compojure.route :refer [not-found]]
            [tokkuri.responses :refer :all]
            [tokkuri.session   :refer :all]
            [tokkuri.exercise  :refer :all]
            [tokkuri.storage.redis :as redis :refer [get-key set-with-expiry]]))

(defn get-or-set-session [key]
  (find-session redis/get-key redis/set-with-expiry key))

(defn get-next-chunk [team session]
  (let [session-value (redis/get-key team)]
    (if (or (nil? team) (not= session session-value))
      (bad-request-response "You need a session token from /t/<team_name>/session")
      ;; perhaps fail / delay
      ;; produce data where session is salt?
      ;; store failure rate, store progress
      ;; store high score?
      (str (get-failure-rate session)))))

(defroutes app
  (context "/t/:team" [team]
           (GET "/session" [] (get-or-set-session team)))
  (context "/s/:session/:team" [session team]
           (GET "/next" [] (get-next-chunk team session)))
  (not-found (not-found-response)))

(def handler
  (-> app
      json-content))
