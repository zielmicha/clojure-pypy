; transforms Clojure sexpr into IR
; see clpy/compiler/__init__.py for overview
(ns clpy.compiler.compiler
  (:use clpy.utils)
  (:use clpy.compiler.syms))

(declare read-from-string
         translate translate-expanded
         translate-function-call
         translate-const
         translate-symbol
         translate-let-basic
         add-with-meta
         make-label
         print-ir
         decorate-with-linum)

(def ^:dynamic *file-name* nil)

(def special-form-translators)

(defn translate-source [src]
  (translate (read-from-string src)))

(defn translate [expr]
  (-> expr
      (macroexpand)
      (translate-expanded)
      ; I don't yet understand where meta should be added
      ;(add-with-meta expr)
      (decorate-with-linum expr)))

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
    ~@(mapcat translate args)
    (call ~(count args))))

(defn translate-const [expr]
  `((const ~expr)))

(defn translate-symbol [expr]
  `((get-var ~expr)))

(def ^:dynamic *last-loop*)
(def ^:dynamic *last-loop-finallies* nil)
(def ^:dynamic *last-loop-var-names*)

(defn with-finally [codefn finally]
  (binding
      [*last-loop-finallies* (concat *last-loop-finallies* finally)]
    (concat
     (codefn)
     finally)))

(def special-form-translators
  {'def (fn [expr ])
   'if (fn [cond if-true & if-false]
         (let [end-label (make-label :end) else-label (make-label :else)]
           `(~@(translate cond)
             (negate)
             (jump-if ~else-label)
             ~@(translate if-true)
             (jump ~end-label)
             (label ~else-label)
             ~@(mapcat translate if-false)
             (label ~end-label))))
   'do (fn [& args]
         args)
   'let* (fn [bindings & exprs]
           (translate-let-basic bindings exprs nil))
   'quote (fn [expr]
            `((const ~expr)))
   'var (fn [name]
          `((get-var-object name)))
   'fn* (fn [& things]
          (defn parse-one-body [things]
              {:args (first things), :exprs (rest things)})

          (let [[things name] (if (symbol? (first things))
                                [(rest things) (first things)]
                                [things nil])
                bodies (map parse-one-body things)]
            `((func {:name ~name :bodies ~bodies}))))

   'loop* (fn [bindings & exprs]
            (binding [*last-loop* (make-label :loop)
                      *last-loop-var-names* (map first (partition 2 bindings))]
              (translate-let-basic bindings exprs `((label ~*last-loop*)))))
   'recur (fn [& args]
            (when-not *last-loop* (throw (Exception. "recur outside of loop")))
            (when-not (= (count args) (count *last-loop-var-names*))
              (throw (Exception. "invalid number of arguments to recur")))
            `(~@*last-loop-finallies*
              ~@(mapcat (fn [name val]
                              `(~@(translate val)
                                (push-local ~name)))
                            *last-loop-var-names* args)
              (jump ~*last-loop*)))
   'try nil})

(defn translate-let-basic [bindings exprs additional]
  (with-finally
    (fn [] `(~@(mapcat (fn [[name value]]
                         `(~@(translate value)
                           (push-local ~name))) (partition 2 bindings))
             ~@additional
             ~@(mapcat translate exprs)
             ))
    (map (fn [[name value]]
             `(pop-local ~name)) (partition 2 bindings))))

(defn add-with-meta [instr src]
  (if (meta src)
    `(~@instr
      (const ~(meta src))
      (with-meta))
    instr))

(def ^:dynamic *label-max-id* (atom 0))

(defn make-label [name]
  [name (swap! *label-max-id* inc)])

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
