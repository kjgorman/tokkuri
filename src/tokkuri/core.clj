(ns tokkuri.core
  (:use [tokkuri.middleware])
  (:require [compojure.core :refer :all]
            [tokkuri.session :refer :all]
            [tokkuri.storage.redis :as redis :refer [get-session set-session]]))

(defn get-or-set-session [key]
  (find-session redis/get-session redis/set-session key))

(defroutes app
  (GET "/" [] "{ \"hello\": \"world\" }")
  (GET "/session/:team"
       [team]
       (get-or-set-session team)))

(def handler
  (-> app
      json-content))
