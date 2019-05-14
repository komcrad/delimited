(ns delimited.cli-test
  (:require [clojure.test :refer :all]
            [delimited.cli :refer :all]
            [delimited.records :refer :all]
            [clojure.string :as s]))

(deftest -main-test
  (testing "-main"
    (let [tmp (java.io.File/createTempFile "delimited-" ".tmp")]
      (spit tmp (str "a,b,male,o,03/16/1994\n" "c,d,female,r,01/19/2014"))
      (is (= (slurp "test/resources/example-output.txt")
             (with-out-str (-main (.getPath tmp)))))
      (.delete tmp))))
