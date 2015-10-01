(ns tokkuri.storage.redis
  (:require [taoensso.carmine :as car :refer (wcar get setex)]))

(def session-ttl-seconds 60)

(def local-instance
  {:pool {}
   :spec { :host "127.0.0.1":port 6379 }
   })

(defmacro wcar* [& body]
  `(car/wcar local-instance ~@body))

(defn get-key [key]
  (wcar* (car/get key)))

(defn get-ttl [key]
  (wcar* (car/ttl key)))

(defn set-with-expiry [key value]
  (do
    (wcar* (car/setex key session-ttl-seconds value))
    value))
