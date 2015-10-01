(ns tokkuri.exercise
  (:require [tokkuri.storage.redis :as redis :refer :all]
            [tokkuri.responses :refer :all]))

(defn- failure-rate-suffix [key]
  (clojure.string/join [key "-failures"]))

(defn- success-rate-suffix [key]
  (clojure.string/join [key "-success"]))

(defn get-success-rate [key]
  (let [skey (success-rate-suffix key)]
    (read-string (or
                  (redis/get-key skey)
                  (redis/set-with-expiry skey "0")))))

(defn get-success-ttl [key]
  (let [skey (success-rate-suffix key)]
    (redis/get-ttl skey)))

(defn set-success-rate [key value]
  (let [skey (success-rate-suffix key)]
    (redis/set-with-expiry skey (str value))))

(defn get-failure-rate [key]
  (let [fkey (failure-rate-suffix key)]
    (read-string (or
                  (redis/get-key fkey)
                  (redis/set-with-expiry fkey "0.25")))))

(defn set-failure-rate [key value]
  (let [fkey (failure-rate-suffix key)]
    (redis/set-with-expiry fkey (str value))))

(defn get-failure-ttl [key]
  (redis/get-ttl (failure-rate-suffix key)))

(defn perhaps-fail [key get-rate]
  (let [rate (get-rate key)
        fail (int (* rate 100))]
    (>= fail (rand-int 100))))

(defn- clamp [value lo hi]
  (Math/max (double lo) (Math/min (double hi) (double value))))

(defn handle-failure [ttl rate set-rate]
  (let [log-scale (Math/exp rate)
        range (- Math/E (* 0.75 log-scale))
        ;; TODO(kjgorman): we shouldn't implicitly rely on the timeout
        ;;                 value here for the ttl conditioning.
        step (* (/ (- ttl 100) 20) range)
        new-rate (clamp (Math/log (* log-scale step)) 0.15 1)]
    (println "adjusting" log-scale range step new-rate)
    (set-rate new-rate)
    (server-error-response)))
