(ns delimited.user
  (:gen-class))

(defmacro sandbox []
  `(do
    (require 'clojure.tools.namespace.repl)
    (clojure.tools.namespace.repl/refresh)
    (use 'clojure.repl)))
