; transforms Clojure sexpr into IR
; see clpy/compiler/__init__.py for overview
(ns clpy.compiler.compiler
  (:use clpy.utils))

(declare read-from-string
         translate translate-expanded
         translate-function-call
         translate-const
         translate-symbol
         translate-let-basic
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
   (and (seq? expr) (contains? special-form-translators (first expr)))
     (apply (fetch special-form-translators (first expr)) (rest expr))
   (seq? expr)
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
  `((get ~expr)))

(def ^:dynamic *last-loop*)
(def ^:dynamic *last-loop-var-names*)

(def special-form-translators
  {'def (fn [expr ])
   'if (fn [cond if-true & if-false]
         (let [end-label (make-label :end) else-label (make-label :else)]
           `(~@(translate cond)
             (jump-if-not ~else-label)
             ~@(translate if-true)
             (jump ~end-label)
             (label ~else-label)
             ~@(apply concat (map translate if-false))
             (label ~end-label))))
   'do (fn [& args]
         args)
   'let* (fn [bindings & exprs]
           (translate-let-basic bindings exprs nil))
   'quote (fn [expr]
            `((const ~expr)))
   'var (fn [name]
            `((get-var-object name)))
   'fn* nil
   'loop* (fn [bindings & exprs]
            (binding [*last-loop* (make-label :loop)
                      *last-loop-var-names* (map second (partition 2 bindings))]
              (translate-let-basic bindings exprs `((label ~*last-loop*)))))
   'recur (fn [& args]
            (when-not *last-loop* (throw (Exception. "recur outside of loop")))
            (when-not (= (count args) (count *last-loop-var-names*))
              (throw (Exception. "invalid number of arguments to recur")))
            `(~@(apply concat
                       (map (fn [name val]
                              `(~@(translate val)
                                (set-local ~name)))
                            *last-loop-var-names* args))
              (jump ~*last-loop*)))
   'try nil})

(defn translate-let-basic [bindings exprs additional]
  `(~@(apply concat
             (map (fn [[name value]]
                    `(~@(translate value)
                      (push-local ~name))) (partition 2 bindings)))
    ~@additional
    ~@(apply concat (map translate exprs))
    ~@(map (fn [[name value]]
             `(pop-local ~name)) (partition 2 bindings))))

(def label-max-id (atom 0))

(defn make-label [name]
  [name (swap! label-max-id inc)])

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