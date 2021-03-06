(ns delimited.cli
  (:gen-class)
  (:require [delimited.records :as r]
            [clojure.pprint :as p]))

(defn pretty-records
  "If by is :gender or :dateofbirth, the order of key values pairs in
   records will be changed to help visualize the order.
   Returns a collection with records displaying the new order."
  [by records]
  (cond
    (= by :gender) (map #(select-keys % [:gender :lastname :firstname
                                         :favoritecolor :dateofbirth])
                        records)
    (= by :dateofbirth) (map #(select-keys % [:dateofbirth :lastname :firstname
                                             :gender :favoritecolor])
                            records)
    :else records))

(defn -main
  "Called by lein run. Slurps the first argument. Assumes the first argument
   contains data parseable by r/records"
  [& args]
  (let [records (r/records (slurp (first args)))
        gender (r/order-by :gender records)
        dateofbirth (r/order-by :dateofbirth records)
        lastname (r/order-by :lastname records)]
    (print "\n\nOrdered by gender:")
    (p/print-table (pretty-records :gender gender))
    (print "\n\nOrdered by date of birth:")
    (p/print-table (pretty-records :dateofbirth dateofbirth))
    (print "\n\nOrdered by last name descending:")
    (p/print-table (pretty-records :lastname lastname))))
