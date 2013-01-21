(ns clpy.utils)

(defn fetch [coll key]
  (let [val (get coll key)]
    (if val val
        (throw (Exception. (format "%s not found in %s" key coll))))))

(defn fetch-in [coll ks]
  (if (seq ks)
    (fetch-in (fetch coll (first ks)) (rest ks))
    coll))

(defn get-symbol-name [sym]
  ; todo
  (let [s (str sym)]
    (if (== (.indexOf s "/") -1)
      s
      (nth (.split s "/") 1))))

(defn hash-map-from-items [pairs]
  {:pre (every? #(= (count %) 2) pairs)}
  (apply hash-map (mapcat identity pairs)))
