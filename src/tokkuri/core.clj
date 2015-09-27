(ns tokkuri.core
  (:use [tokkuri.middleware])
  (:require [compojure.core :refer :all]
            [tokkuri.session :refer :all]
            [tokkuri.storage.redis :as redis :refer [get-session set-session]]))

(defn get-or-set-session [address]
  (find-session redis/get-session redis/set-session address))

(defroutes app
  (GET "/" [] "{ \"hello\": \"world\" }")
  (GET "/session" { :keys [remote-addr] } (get-or-set-session remote-addr)))

(def handler
  (-> app
      json-content))
