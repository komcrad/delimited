(ns delimited.records-test
  (:require [clojure.test :refer :all]
            [delimited.records :as r]
            [clojure.string :as s]
            [clj-time.format :as f]
            [clj-time.core :as t]))


(defn random-record [delimiter]
  (s/join delimiter
          [(rand-nth (s/split-lines (slurp "test/resources/last-names.txt")))
           (rand-nth (s/split-lines (slurp "test/resources/first-names.txt")))
           (rand-nth ["male" "female"])
           (rand-nth (s/split-lines (slurp "test/resources/colors.txt")))
           (f/unparse (f/formatter "MM/dd/yyyy")
                      (t/date-time (+ 1919 (rand-int 100))
                                   (+ 1 (rand-int 12))
                                   (+ 1 (rand-int 29))))]))

(defn gen-delimited-file [delimiter n file]
  (spit file (s/join "\n" (take n (repeatedly #(random-record delimiter))))))

(deftest del-spaces-test
  (testing "del-spaces"
    (is (= "name,last,date" (r/del-spaces "name, last   ,date")))
    (is (= "name|last|date" (r/del-spaces "name |last   |date")))
    (is (= "name last date" (r/del-spaces "name last date")))
    (is (= "name last date" (r/del-spaces "name last  date")))))

(deftest record-test
  (testing "record"
    (is (thrown? java.lang.Exception (r/record "Smail, Erma, male, Flame-Pea")))
    (is (= {:lastname "Smail" :firstname "Erma" :gender "male"
            :favoritecolor "Flame-Pea" :dateofbirth "10/19/1969"}
           (r/record "Smail, Erma, male, Flame-Pea, 10/19/1969")))
    (is (= {:lastname "Smail" :firstname "Erma" :gender "male"
            :favoritecolor "Flame-Pea" :dateofbirth "10/19/1969"}
           (r/record "Smail | Erma | male | Flame-Pea | 10/19/1969")))
    (is (= {:lastname "Smail" :firstname "Erma" :gender "male"
            :favoritecolor "Flame-Pea" :dateofbirth "10/19/1969"}
           (r/record "Smail   | Erma |   male | Flame-Pea |   10/19/1969")))
    (is (= {:lastname "Smail" :firstname "Erma" :gender "male"
            :favoritecolor "Flame-Pea" :dateofbirth "10/19/1969"}
           (r/record "Smail,Erma,male | Flame-Pea |   10/19/1969")))))

(deftest records-test
  (testing "records"
    (is (= (list {:lastname "Smail" :firstname "Erma" :gender "male"
                  :favoritecolor "Flame-Pea" :dateofbirth "10/19/1969"})
           (r/records "Smail, Erma, male, Flame-Pea, 10/19/1969")))
    (is (= (list {:lastname "Smail" :firstname "Erma" :gender "male"
                  :favoritecolor "Flame-Pea" :dateofbirth "10/19/1969"}
                 {:lastname "Carpet" :firstname "Ermengarde" :gender "female"
                  :favoritecolor "Rose" :dateofbirth "05/10/1970"})
           (r/records (str "Smail, Erma, male, Flame-Pea, 10/19/1969\n"
                           "Carpet, Ermengarde, female, Rose, 05/10/1970"))))))
(deftest order-by-test
  (testing "order-by"
    (let [recs (r/records (str "a,b,female,b,05/23/1984\n"
                               "c,d,male,g,06/25/1981"))]
      (is (= '("c" "a")
             (map :lastname (r/order-by :lastname recs)))))
    (let [recs (r/records (str "c,d,male,g,06/25/1981\n"
                               "e,f,female,r,05/24/1990\n"
                               "a,b,female,b,05/23/1984\n"
                               "g,h,male,y,03/12/2001"))]
      (is (= '("a" "e" "c" "g")
             (map :lastname (r/order-by :gender recs)))))
    (let [recs (r/records (str "a,b,female,b,05/23/1984\n"
                               "c,d,male,g,06/25/1981\n"
                               "g,h,male,g,06/25/1983\n"
                               "e,f,male,g,06/25/1982\n"))]
      (is (= '("c" "e" "g" "a")
             (map :lastname (r/order-by :dateofbirth recs)))))
    (let [recs (r/records (str "a,b,female,b,05/23/1984\n"
                               "e,f,male,g,06/25/1982\n"))]
      (is (thrown? Exception (r/order-by :name recs))))))
