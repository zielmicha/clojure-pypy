(ns clpy.tests.compiler-tests
  (:use clpy.compiler.compiler))

(binding [*file-name* "compile-tests.clj"]
    (print-ir (translate '(foobar eghr)))
    (print-ir (translate '(if a b c)))
    (print-ir (translate '(if a b)))
    (print-ir (translate '(if a (foo (if d e)) c))))

(binding [*file-name* "compile-tests.clj"]
    (print-ir (translate '(let [a b] c)))
    (print-ir (translate '(loop [a b] c)))
    (print-ir (translate '(loop [a b]
                            (recur 1)))))
