(ns clpy.utils)

(defn fetch [coll key]
  (let [val (get coll key)]
    (if val val
        (throw (Exception. (format "%s not found in %s" key coll))))))

(defn get-symbol-name [sym]
  ; todo
  (let [s (str sym)]
    (if (== (.indexOf s "/") -1)
      s
      (nth (.split s "/") 1))))
