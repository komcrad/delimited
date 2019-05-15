(ns delimited.records
  (:gen-class)
  (:require [clojure.string :as s]
            [clj-time.format :as f]
            [clj-time.core :as t]))

(defn del-spaces
  "Removes extra spaces from s"
  [s]
  (cond
    (re-find #",|\|" s) (s/replace s #" " "")
    :else (s/replace s #"\s+" " ")))

(defn record
  "Takes a string of one record containing 5 delimited fields.
   Each of the five fields of the record is separated by space, comma, or pipe.
   zips the fields into a map with keys :lastname, :firstname, :gender,
   :favoritecolor, and dateofbirth."
  [s]
  (let [fields (s/split (del-spaces s) #",|\||\ ")]
    (if (= 5 (count fields))
      (zipmap [:lastname :firstname :gender :favoritecolor :dateofbirth]
              fields) 
      (throw (Exception. (str "Record " fields " must have 5 fields:\n"))))))

(defn records
  "Takes a string of delimited records seperated with newlines.
   Returns a sequence of records."
  [s]
  (map record (s/split-lines s)))

(defn date
  "Parses our date format MM/dd/yyyy into a date object for comparisons"
  [s]
  (f/parse (f/formatter "MM/dd/yyyy") s))

(defn order-by
  "returns a new collection of records sorted by k
   where k is :lastname, :gender, :dateofbirth"
  [k records]
  (cond
    (= :lastname k) (sort-by :lastname #(compare %2 %1) records)
    (= :gender k) (sort-by :gender (reverse (order-by :lastname records)))
    (= :dateofbirth k)
    (sort-by :dateofbirth #(t/before? (date %1) (date %2)) records)
    :else (throw (Exception. (str "order-by only recognizes :lastname, "
                                  ":dateofbirth, or :gender keys")))))
