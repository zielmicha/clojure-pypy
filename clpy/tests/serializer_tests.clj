(ns clpy.tests.serializer-tests
  (:use clpy.serializer)
  (:use clojure.test))

;; (binding [*print-dup* true] ; print Strings Python way
;;           (println (serialize-to-str "foobar"))
;;           (println (serialize-to-str (list :w "foobar")))
;;           )

(deftest test-serialize
  (is (= (serialize-to-str "foobar") "s\n6\nfoobar"))
  (is (= (serialize-to-str (list :w "foobar")) "L\n2\nK\n2\n:w\ns\n6\nfoobar"))
  (is (= (serialize-to-str 'a) "S\n1\na"))
  (is (= (serialize-to-str {'a 'b}) "M\n1\nV\n2\nS\n1\na\nS\n1\nb"))
  (is (= (serialize-to-str 5) "I\n5"))
  (is (= (serialize-to-str #{1}) "?\nV\n2\nc\nclojure.lang.PersistentHashSet\nV\n1\nI\n1")))

(run-tests)
;(println (serialize-to-str #{1}))
