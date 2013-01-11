; transforms Clojure sexpr into IR
; see clpy/compiler/__init__.py for overview
(ns clpy.compiler.compiler
  (:use clpy.utils))

(declare read-from-string
         translate translate-expanded
         translate-function-call
         translate-const
         translate-symbol
         make-label
         print-ir
         decorate-with-linum)

(def ^:dynamic *file-name* nil)

(def special-form-translators)

(defn translate-source [src]
  (translate (read-from-string src)))

(defn translate [expr]
  (decorate-with-linum (translate-expanded (macroexpand expr)) expr))

(defn translate-expanded [expr]
  (cond
   (and (list? expr) (contains? special-form-translators (first expr)))
     (apply (fetch special-form-translators (first expr)) (rest expr))
   (list? expr)
     (translate-function-call expr)
   (symbol? expr)
     (translate-symbol expr)
   :else
     (translate-const expr)))

(defn translate-function-call [[name & args]]
  `(~@(translate name)
    ~@(apply concat (map translate args))
    (call ~(count args))))

(defn translate-const [expr]
  `((const ~expr)))

(defn translate-symbol [expr]
  `((get-var ~expr)))

(def special-form-translators
  {'def (fn [expr ])
   'if (fn [cond if-true & if-false]
         (let [end-label (make-label) else-label (make-label)]
           `(~@(translate cond)
             (jumpifnot else-label)
             ~@(translate if-true)
             (jump end-label)
             (label else-label)
             ~@(translate `(do ~@if-false))
             (label end-label))))
   'do (fn [& args]
         args)
   'let* (fn [bindings & exprs]
           (apply concat
                  (map (partition 2 bindings)
                       (fn [[name value]]
                         `(~@(translate value)
                           (set-local ~name))))))
   'quote (fn [expr]
            `((const ~expr)))
   'var nil
   'fn* nil
   'loop nil
   'recur nil
   'throw nil
   'try nil})

(def label-max-id (atom 0))

(defn make-label []
  {::label (swap! label-max-id inc)})

(defn print-ir [l]
  (doseq [item l]
    (apply println
           (str (:line (meta item)) ":")
           (get-symbol-name (first item))
           (rest item))))

(defn decorate-with-linum [new old]
  (if (and (seq new) (meta old) (:line (meta old)))
    (map #(with-meta % {:line (:line (meta old)) :fn *file-name*}) new)
    new))

(defn read-from-string [str]
  (with-in-str str (read)))
