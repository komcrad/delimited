(ns delimited.records
  (:gen-class)
  (:require [clojure.string :as s]
            [clj-time.format :as f]
            [clj-time.core :as t]))

(defn del-spaces [s]
  (cond
    (re-find #",|\|" s) (s/replace s #" " "")
    :else (s/replace s #"\s+" " ")))

(defn record [s]
  (let [fields (s/split (del-spaces s) #",|\||\ ")]
    (if (= 5 (count fields))
      (zipmap [:lastname :firstname :gender :favoritecolor :dateofbirth]
              fields) 
      (throw (Exception. (str "Record " fields " must have 5 fields:\n"))))))

(defn records [s]
  (map record (s/split-lines s)))

(defn date [s]
  (f/parse (f/formatter "MM/dd/yyyy") s))

(defn order-by [k records]
  (cond
    (= :lastname k) (sort-by :lastname #(compare %2 %1) records)
    (= :gender k) (sort-by :gender (reverse (order-by :lastname records)))
    (= :dateofbirth k)
    (sort-by :dateofbirth #(t/after? (date %1) (date %2)) records)
    :else (throw (Exception. (str "order-by only recognizes :lastname, "
                                  ":dateofbirth, or :gender keys")))))
