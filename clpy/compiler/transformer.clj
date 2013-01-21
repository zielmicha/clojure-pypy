; transforms IR into bytecode
; see clpy/compiler/__init__.py for overview
(ns clpy.compiler.transformer
  (:use clpy.utils)
  (:use clpy.compiler.syms)
  (:require [clpy.graph :as graph]))

(declare graph-from-ir
         graph-from-ir-and-labels
         get-labels-in-code)

(defn graph-from-ir [xs]
  (let [labels (get-labels-in-code xs)]
    (graph-from-ir-and-labels labels xs)))

(defn graph-from-ir-and-labels [labels xs]
  (if (seq xs)
    (let [item (first xs)
          vertex (condp = (first item)
                   `label (fetch labels (second item))
                   `jump (graph/vertex `(nop ~'jump))
                   `jump-if (graph/vertex `(jump-if))
                   (graph/vertex item))
          next-item (graph-from-ir-and-labels labels (rest xs))
          get-label (fn []
                      (graph/merge-edges (fetch labels (second item)) next-item))
          edges (condp = (first item)
                  `jump {:next (get-label)}
                  `jump-if {:true (get-label)
                            :false next-item}
                  {:next next-item})]
      (graph/with-edges vertex edges))
    (graph/vertex `(end))))

(defn get-labels-in-code [xs]
  (hash-map-from-items
    (map #(let [name (second %)] [name (graph/vertex `(nop ~name))])
         (filter #(= (first %) `label) xs))))
