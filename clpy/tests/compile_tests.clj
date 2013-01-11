(ns clpy.tests.compiler-tests
  (:use clpy.compiler.compiler))

(binding [*file-name* "compile-tests.clj"]
    (print-ir (translate '(foobar eghr))))
