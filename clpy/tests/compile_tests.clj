(ns clpy.tests.compiler-tests
  (:use clpy.compiler.compiler)
  (:refer-clojure :exclude [test]))

(defn test [expr]
  (println)
  (println expr)
  (print-ir (translate expr)))

(binding [*file-name* "compile-tests.clj"]
    (test '(foobar eghr))
    (test '(if a b c))
    (test '(if a b))
    (test '(if a (foo (if d e)) c))


    (test '(let [a b] c))
    (test '(loop [a b] c))
    (test '(loop [a b]
             (let [c 1]
               (recur 5))))
    (test '(loop [a b]
             (recur 1)))


    (test '(fn [a b]
             foo))
    (test '(fn
             ([a b] foo)
             ([a] bar)))

    (test '(do a b c)))
