(ns tokkuri.exercise-test
  (:require [clojure.test :refer :all]
            [tokkuri.exercise :refer :all]))

(defn- circa [a b epsilon]
  (< (Math/abs (- a b)) epsilon))

(deftest failure-settings
  (testing "failing to find a chunk will invoke the set-rate function"
    (handle-failure 100 0.1 (fn [_] (is true)))))

(deftest fail-incrementing
  (testing "failing to find a chunk will set a new, increased, failure rate"
    (handle-failure 100 0.1 (fn [new-rate]
                              (is (> new-rate 0.1))))))

(deftest fail-logarithmically
  (testing "it increases on a log scale"
    (handle-failure 100 0.1 (fn [new-rate]
                              (is (circa 0.895 new-rate 0.01))))))

(deftest perhaps-failure-never
  (testing "if the failure rate is set to zero then we cannot fail"
    (is (= false (perhaps-fail 1 (fn [_] 0))))))

(deftest perhaps-failure-always
  (testing "if the failure rate is set to 1.0 then we always fail"
    (is (= true (perhaps-fail 1 (fn [_] 1.0))))))
