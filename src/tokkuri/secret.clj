(ns tokkuri.secret
  (require [pandect.algo.sha1 :refer [sha1-hmac]]
           [tokkuri.storage.redis :as redis :refer [get-ttl]]
           [tokkuri.responses :refer :all]))

(def secret [
             "I keep buyin’ shit" "just make sure you keep track of it all"
             "I got bitches askin’" "me about the code for the Wi-Fi"
             "So they can talk about" "they Timeline"
             "And show me pictures" "of they friends"
             "Just to tell me they" "ain’t really friends"
             "Ex-girl, she the" "female version of me"
             "I got strippers in" "my life, but they virgins to me"
             "I hear everybody talkin'" "'bout what they gon' be"
             ])

(def passcode "asterix")

(defn- next-chunk [progress]
  (if (= (int progress) 16)
    passcode
    (nth secret progress)))

(defn- hash-chunk [chunk session-key]
  (sha1-hmac chunk session-key))

(defn get-secret-for [progress session-key]
  (-> (next-chunk progress) (hash-chunk session-key)))

(defn- winners [session-key]
  (let [remaining (redis/get-ttl session-key)]
    (success-response (clojure.string/join ["you got it with " (str remaining) " seconds remaining"]))))

(defn perhaps-winners [session submitted]
  (if (= submitted (sha1-hmac passcode session))
    (winners session)
    (bad-request-response "incorrect attempt")))
