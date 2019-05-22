(ns delimited.server
  (:gen-class)
  (:require [delimited.records :as r]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.server :as http]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [cheshire.core :refer [generate-string]]))

; this is what will store the records
(defonce records (atom `()))

(defn records-str
  "Converts coll to json and wraps it as a value to a :people key"
  [coll]
  (generate-string {:people coll} {:pretty true}))

(defn insert-record!
  "Takes a map and inserts the record found in :param :record
   into our records atom"
  [req]
  (swap! records conj (r/record (get-in req [:params :record])))
  {:status 200
   :body "SUCCESS"})

; the by-something functions return a map where :body
; is the jsonified value of the records atom ordered by something
(defn by-gender []
  {:status 200
   :body (records-str (r/order-by :gender @records))})

(defn by-name []
  {:status 200
   :body (records-str (r/order-by :lastname @records))})

(defn by-birthdate []
  {:status 200
   :body (records-str (r/order-by :dateofbirth @records))})

; compojure routing...
(defroutes app-routes
  (POST "/records" req (insert-record! req))
  (GET "/records/gender" [] (by-gender))
  (GET "/records/birthdate" req (by-birthdate))
  (GET "/records/name" req (by-name))
  (route/not-found "Not found"))

; called by lein with-profile web run
(defn -main [& args]
  (let [handler (wrap-defaults app-routes
                               (assoc-in site-defaults
                                         [:security :anti-forgery] false))]
    (http/run-server handler {:port 8080})
    (println "listening on port 8080")))
