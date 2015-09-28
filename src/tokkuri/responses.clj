(ns tokkuri.responses)

(defn with-status [status body]
  {:status status
   :body body })

(defn not-found-response []
  (with-status 404 "{ \"error\": \"not found\" }"))

(defn bad-request-response [message]
  (with-status 400 (clojure.string/join ["{ \"reason\": \"" message "\" }"])))
