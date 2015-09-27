(ns tokkuri.storage.redis
  (:require [taoensso.carmine :as car :refer (wcar get setex)]))

(def session-ttl-seconds 120)

(def local-instance
  {:pool {}
   :spec { :host "127.0.0.1":port 6379 }
   })

(defmacro wcar* [& body]
  `(car/wcar local-instance ~@body))

(defn get-session [session-key]
  (wcar* (car/get session-key)))

(defn set-session [session-key session-value]
  (wcar* (car/setex session-key session-ttl-seconds session-value)))
