(ns tokkuri.session
  (require [pandect.algo.sha1 :refer [sha1-hmac]]))

(def salt "snakeskin")

(defn- nix-time []
  (quot (System/currentTimeMillis) 1000))

(defn- create-session [setter key]
  (let [hash (-> (str (nix-time)) (sha1-hmac salt))]
    (do
      (setter key hash)
      hash)))

(defn find-session [lookup setter key]
  (or (lookup key) (create-session setter key)))
