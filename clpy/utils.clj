(ns clpy.utils)

(defn fetch [coll key]
  (let [val (get coll key)]
    (if val val
        (throw (Exception. (format "%s not found in %s" key (class coll)))))))

(defn fetch-in [coll ks]
  (if (seq ks)
    (fetch-in (fetch coll (first ks)) (rest ks))
    coll))

(defn fetch-first [coll]
  (if (seq coll)
    (first coll)
    (throw (Exception. "(fetch-first nil)"))))

; update-in exists, so why not update?
(defn update [coll k func]
  (update-in coll [k] func))

(defn get-symbol-name [sym]
  ; todo
  (let [s (str sym)]
    (if (== (.indexOf s "/") -1)
      s
      (nth (.split s "/") 1))))

(defn hash-map-from-items [pairs]
  {:pre (every? #(= (count %) 2) pairs)}
  (apply hash-map (mapcat identity pairs)))

(defn pop-n [stack n]
  (let [[front stack] (loop [stack stack, n n, front nil]
             (if (= n 0)
               [front stack]
               (recur (rest stack) (dec n) (cons (first stack) front))))]
    [(reverse front) stack]))
