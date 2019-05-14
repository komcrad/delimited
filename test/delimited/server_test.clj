(ns delimited.server-test
  (:require [clojure.test :refer :all]
            [delimited.server :refer :all]
            [delimited.records :as r]
            [clojure.data.json :as json]
            [clojure.string :as s]))

(deftest records-str-test
  (testing "records-str"
    (is (= {"people" {"name" "bob"}}
           (json/read-str (records-str {:name "bob"}))))))

(deftest insert-record!-test
  (testing "insert-record!"
    (reset! records '())
    (is (= "SUCCESS" (:body (insert-record!
                              {:query-string "a,b,male,c,01/01/2001"}))))
    (is (= '({:lastname "a" :firstname "b" :gender "male" :favoritecolor "c"
              :dateofbirth "01/01/2001"})))
    (is (= "SUCCESS" (:body (insert-record!
                              {:query-string "a,c,female,c,01/01/2001"}))))
    (is (= '({:lastname "a" :firstname "c" :gender "female" :favoritecolor "c"
              :dateofbirth "01/01/2001"}
             {:lastname "a" :firstname "b" :gender "male" :favoritecolor "c"
              :dateofbirth "01/01/2001"})))
    (reset! records '())))

(deftest by-something
  (testing "by-gender, by-name, by-birthdate"
    (do
    (reset! records '())
    (is (= "SUCCESS" (:body (insert-record!
                              {:query-string "a,b,male,c,01/01/2000"}))))
    (is (= "SUCCESS" (:body (insert-record!
                              {:query-string "b,c,male,c,01/01/2001"}))))
    (is (= "SUCCESS" (:body (insert-record!
                              {:query-string "d,e,female,c,01/01/1999"}))))
    (is (= "SUCCESS" (:body (insert-record!
                              {:query-string "f,g,female,c,01/01/1998"}))))
    (let [people (get (json/read-str (:body (by-gender))) "people")]
      (is (= '("d" "f" "a" "b") (map #(get % "lastname") people))))
    (let [people (get (json/read-str (:body (by-name))) "people")]
      (is (= '("f" "d" "b" "a") (map #(get % "lastname") people))))
    (let [people (get (json/read-str (:body (by-birthdate))) "people")]
      (is (= '("f" "d" "a" "b") (map #(get % "lastname") people))))
    (reset! records '()))))
