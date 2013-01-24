; Immutable, but not functional data structure representing
; graph with cycles.
; Current implementation has pathetic comlexity guarantees.
(ns clpy.graph
  (:use clpy.utils))

(defrecord Vertex [id edges values])

(def ^:dynamic *next-id* (atom 0))

(declare vertex
         with-edge
         get-edge-from
         get-edge
         get-value
         merge-edges)

(defn vertex [value]
  (let [id (swap! *next-id* inc)]
    (Vertex. id {} {id value})))

(defn with-edge [vertex to value]
  (->
   (merge-edges vertex to)
   (assoc-in [:edges (:id vertex) value] (:id to))))

(defn merge-edges [vertex with]
  (assoc vertex
    :edges (merge (:edges vertex) (:edges with)) ; todo: this should detect collisions
    :values (merge (:values vertex) (:values with))))

(defn with-edges [vertex hashmap]
  (if (seq hashmap)
    (let [[k v] (first hashmap)]
      (recur (with-edge vertex v k) (rest hashmap)))
    vertex))

(defn with-value [vertex new-value]
  (assoc-in vertex [:values (:id vertex)] new-value))

(defn get-edge-from [vertex from value]
  (Vertex. (fetch-in vertex [:edges (:id from) value])
           (:edges vertex)
           (:values vertex)))

(defn get-edge [vertex value]
  (get-edge-from vertex vertex value))

(defn get-edge-keys-from [vertex from]
  (keys (get-in vertex [:edges (:id from)])))

(defn get-edge-keys [vertex]
  (get-edge-keys-from vertex vertex))

(defn get-value [vertex]
  (get (:values vertex) (:id vertex)))

(defn update-edges [vertex fun]
  (loop [vertex vertex, keys (get-edge-keys vertex)]
    (if (seq keys)
      (let [key (first keys), edge-to (get-edge vertex key)]
        (recur (with-edge vertex (fun edge-to) key) (rest keys)))
      vertex)))

(defn walk-with-state [state vertex fun & {:keys [skip] :or {skip #{}}}]
  ; DFS
  {:pre [(= (class vertex) Vertex)]
   :post [(= (class %) Vertex)]}
  (if (contains? skip (:id vertex))
    vertex
    (let [new-skip (conj skip (:id vertex))]
      (update-edges vertex
                    (fn [item]
                      (let [[new-state new-item] (fun state item)]
                        (assert (= (class new-item) Vertex)
                                "Walker didn't return Vertex.")
                        (walk-with-state new-state new-item fun :skip new-skip) ))))))

(defn walk [vertex fun]
  (walk-with-state nil vertex (fn [state vertex]
                                [state (fun vertex)])))

(defn print-graph [vertex & {:keys [indent refs] :or {indent "" refs #{}}}]
  (if (contains? refs (:id vertex))
    (println indent "-> ref" (:id vertex))
    (do
      (println indent "node" (get-value vertex) "; id" (:id vertex))
      (let [keys (get-edge-keys vertex)
            refs (conj refs (:id vertex))]
        (if (= (count keys) 1)
          (print-graph (get-edge vertex (first keys)) :indent indent :refs refs)
          (let [new-indent (str indent "   ")]
            (doseq [key keys]
              (println indent "  key:" key)
              (print-graph (get-edge vertex key) :indent new-indent :refs refs))))))))
