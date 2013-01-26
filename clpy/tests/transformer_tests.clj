(ns clpy.tests.compiler-tests
  (:use clpy.compiler.compiler)
  (:use clpy.compiler.transformer)
  (:use clpy.compiler.syms)
  (:use clpy.graph)
  (:refer-clojure :exclude [test]))

(defn translate-and-graph [sexpr]
  (graph-from-ir (translate sexpr)))

(defn test [code]
  (->
   code
   (translate)
   (graph-from-ir)
   (eliminate-nops)
   (print-graph))
  (->
   code
   (translate)
   (graph-from-ir)
   (eliminate-nops)
   (stack-to-register)
   (eliminate-nops)
   (print-graph)))

(test
 '(loop [a 1]
    (recur (inc a))))
