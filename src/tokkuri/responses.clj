(ns tokkuri.responses)

(defn with-status [status body]
  {:status status
   :body body })

(defn not-found-response []
  (with-status 404 "{ \"error\": \"not found\" }"))

(defn bad-request-response [message]
  (with-status 400 (clojure.string/join ["{ \"reason\": \"" message "\" }"])))

(defn unauthorised-response [message]
  (with-status 401 (clojure.string/join ["{ \"reason\": \"" message "\" }"])))

(defn server-error-response []
  (with-status 500 "{ \"error\": \"failed to read\" }"))

(defn success-response [chunk]
  (with-status 200 (clojure.string/join ["{ \"success\": \"" chunk "\" }"])))
