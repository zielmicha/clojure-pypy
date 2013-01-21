(ns clpy.tests.compiler-tests
  (:use clpy.compiler.compiler)
  (:use clpy.compiler.transformer)
  (:use clpy.compiler.syms)
  (:use clpy.graph))

(defn translate-and-graph [sexpr]
  (graph-from-ir (translate sexpr)))


(print-graph (translate-and-graph '(if a b c)))
(print-graph (translate-and-graph '(loop [a 1]
                                     (recur 2))))
(print-graph (translate-and-graph '(loop [a 1]
                                     (recur (inc a)))))