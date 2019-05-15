(ns delimited.server
  (:gen-class)
  (:require [delimited.records :as r]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.server :as http]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.data.json :as json]))

(defonce records (atom `()))

(defn records-str [coll]
  (json/write-str {:people coll}))

(defn insert-record! [req]
  (swap! records conj (r/record (get-in req [:params :record])))
  {:status 200
   :body "SUCCESS"})

(defn by-gender []
  {:status 200
   :body (records-str (r/order-by :gender @records))})

(defn by-name []
  {:status 200
   :body (records-str (r/order-by :lastname @records))})

(defn by-birthdate []
  {:status 200
   :body (records-str (r/order-by :dateofbirth @records))})

(defroutes app-routes
  (POST "/records" req (insert-record! req))
  (GET "/records/gender" [] (by-gender))
  (GET "/records/birthdate" req (by-birthdate))
  (GET "/records/name" req (by-name))
  (route/not-found "Not found"))

(defn -main [& args]
  (let [handler (wrap-defaults app-routes
                               (assoc-in site-defaults
                                         [:security :anti-forgery] false))]
    (http/run-server handler {:port 8080})
    (println "listening on port 8080")))
