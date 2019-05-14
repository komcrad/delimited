(defproject delimited "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-time "0.15.0"]
                 [http-kit "2.3.0"]
                 [compojure "1.6.1"]
                 [org.clojure/data.json "0.2.6"]
                 [ring/ring-defaults "0.3.2"]]
  :main ^:skip-aot delimited.cli
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[komcrad/lein-autoreload "0.2.0"]
                             [lein-cloverage "1.1.1"]]
                   :repl-options {:init-ns delimited.user
                                  :init (delimited.user/sandbox)}}
             :web {:main ^:skip-aot delimited.server}})

