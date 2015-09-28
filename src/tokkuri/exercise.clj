(ns tokkuri.exercise
  (:require [tokkuri.storage.redis :as redis :refer :all]))

(defn- failure-rate-suffix [key]
  (clojure.string/join [key "-failures"]))

(defn get-failure-rate [key]
  (let [fkey (failure-rate-suffix key)]
    (read-string (or
                  (redis/get-key fkey)
                  (redis/set-with-expiry fkey "0.1")))))
