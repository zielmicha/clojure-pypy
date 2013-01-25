(ns clpy.serializer)

(declare custom-reduce
         custom-unreducers
         serialize
         serialize-seqable
         serialize-stringable
         serialize-default)

(defn serialize-to-str [thing]
  (clojure.string/join "\n" (serialize thing)))

(defn serialize [thing]
  (condp #(isa? %2 %1) (class thing)
    clojure.lang.LazySeq (serialize-seqable "L" thing)
    clojure.lang.IPersistentList (serialize-seqable "L" thing)
    clojure.lang.IPersistentVector (serialize-seqable "V" thing)
    clojure.lang.IPersistentMap (serialize-seqable "M" thing)
    clojure.lang.Keyword (serialize-stringable "K" thing)
    clojure.lang.Symbol (serialize-stringable "S" thing)
    java.lang.String (serialize-stringable "s" thing)
    java.lang.Integer (list "I" (str thing))
    java.lang.Long (list "I" (str thing))
    java.lang.Number (list "F" (str thing))
    java.lang.Class (list "c" (.getName thing))
    (serialize-default thing)))

(defn serialize-default [thing]
  (concat (list "?") (serialize (custom-reduce thing))))

(defn serialize-seqable [type thing]
  (concat (list type (count thing)) (mapcat serialize thing)))

(defn serialize-stringable [type thing]
  (list type (str (.length (str thing))) (str thing)))

(defmulti custom-reduce class)
(def custom-unreducers (atom {}))

(defn defcustomserializer [type & {:keys [reduce unreduce]}]
  (swap! custom-unreducers #(assoc % (.getName type) unreduce))
  (.addMethod custom-reduce type reduce))

(defcustomserializer clojure.lang.PersistentHashSet
  :reduce
  (fn [thing] [clojure.lang.PersistentHashSet (apply vector thing)])
  :unreduce
  (fn [thing] (apply hash-set thing)))
