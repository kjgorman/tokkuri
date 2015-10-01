(ns tokkuri.core
  (:use [tokkuri.middleware])
  (:require [compojure.core  :refer :all]
            [compojure.route :refer [not-found]]
            [tokkuri.responses :refer :all]
            [tokkuri.session   :refer :all]
            [tokkuri.exercise  :refer :all]
            [tokkuri.secret    :refer :all]
            [tokkuri.storage.redis :as redis :refer :all]))

(defn- just-delay [timeout response]
  (Thread/sleep (* timeout 1000))
  (response))

(defn- get-or-set-session [key]
  (find-session redis/get-key redis/set-with-expiry key))

(defn- handle-success [session-value]
  (let [progress (get-success-rate session-value)
        next-chunk (get-secret-for progress session-value)]
    (set-success-rate session-value (inc progress))
    (if (> 20 (rand-int 100))
      (just-delay (rand-int 2) (fn [] (success-response next-chunk)))
      (success-response next-chunk))))

(defn- get-failure [session]
  (let [maybe-timeout (rand-int 100)]
    (cond
      (> 20 maybe-timeout)
         (just-delay (rand-int 10) server-error-response)
      (> 5 maybe-timeout)
         (just-delay (rand-int 30) server-error-response)
      :else
      (let [ttl (get-failure-ttl session)
            rate (get-failure-rate session)]
        (handle-failure ttl rate (fn [value] (set-failure-rate session value)))))))

(defn- decide-response [session-value]
  (let [failed (perhaps-fail session-value get-failure-rate)]
    (if failed
      (get-failure session-value)
      (handle-success session-value))))

(defn get-next-chunk [team session-request]
  (let [session-value (redis/get-key team)]
    (if (or (nil? team) (not= session-request session-value))
      (bad-request-response "You need a session token from /t/<team_name>/session")
      (decide-response session-value))))

(defroutes app
  (context "/t/:team" [team]
           (GET "/session" [] (get-or-set-session team)))
  (context "/s/:session/:team" [session team]
           (GET "/next" [] (get-next-chunk team session)))
  (not-found (not-found-response)))

(def handler
  (-> app
      json-content))
