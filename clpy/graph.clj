; immutable, but not functional data structure representing
; graph with cycles
(ns clpy.graph
  (:use clpy.utils))

(defrecord Vertex [id edges values])

(def ^:dynamic *next-id* (atom 0))

(declare vertex
         with-edge
         get-edge-from
         get-edge
         get-value)

(defn vertex [value]
  (let [id (swap! *next-id* inc)]
    (Vertex. id {} {id value})))

(defn with-edge [vertex from to value]
  (->
   vertex
   (assoc-in [:edges (:id from) value] (:id to))
   (assoc-in [:values (:id from)] (get-value from))
   (assoc-in [:values (:id to)] (get-value to))))

(defn get-edge-from [vertex from value]
  (Vertex. (fetch-in vertex [:edges (:id from) value])
           (:edges vertex)
           (:values vertex)))

(defn get-edge [vertex value]
  (get-edge-from vertex vertex value))

(defn get-value [vertex]
  (get (:values vertex) (:id vertex)))
