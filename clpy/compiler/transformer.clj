; transforms stack-based IR into register-based IR
; see clpy/compiler/__init__.py for overview
(ns clpy.compiler.transformer
  (:use clpy.utils)
  (:use clpy.compiler.syms)
  (:require [clpy.graph :as graph]))

(declare graph-from-ir
         graph-from-ir-and-labels
         get-labels-in-code)

(defn graph-from-ir
  "Create a graph from list-based IR.
  Returns Vertex from clpy.graph."
  [xs]
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

(defn get-labels-in-code
  "Scans IR searching for labels.
  Returns mapping from label names to Vertices."
  [xs]
  (hash-map-from-items
    (map #(let [name (second %)] [name (graph/vertex `(nop ~name))])
         (filter #(= (first %) `label) xs))))

(defn eliminate-nops [vertex]
  "Removes nops from graph."
  (graph/walk vertex (fn [v]
                       (let [value (graph/get-value v)]
                         (if (= (first value) `nop)
                           (graph/get-edge v :next)
                           v)))))

(defrecord Variable [register value-known value])
(defrecord State [variables max-reg stack])

(declare stack-to-register
         std-instruction
         std-instruction-c)

(defn instr
  "Create register-based instruction object."
  [name d]
  {:pre [(every? (partial every? integer?)
                 [(:in d) (:out d)])]}
  (list name (hash-map-from-items (filter (fn [[k v]] (not= v [])) d))))

(defn std-instruction
  "Instruction that only pushes and pops from stack.
  Takes function that should return map with keys
  :in and :out with number of registers."
  [func]
  (fn [state name value]
    (let [{:keys [in-count out-count]} (func value)
          stack (:stack state)
          [in-reg stack] (pop-n stack in-count)

          out-reg (range (+ 1 (:max-reg state))
                         (+ 1 out-count (:max-reg state)))
          state (update state :max-reg (partial + out-count))
          stack (concat stack out-reg)

          state (assoc state :stack stack)]
      [state
       (instr name {:value value
                    :in (apply vector in-reg)
                    :out (apply vector out-reg)})])))

(defn std-instruction-c
  "Shorthand for std-instruction with constant function as argument."
  [& {:keys [in out]}]
  (std-instruction (fn [value] {:in-count in :out-count out})))

(def ^{:doc "Mapping holding functions for processing stack-based
  instructions into register-based.

  They should take arguments [state name value] and return tuple
  [new-state (new-name new-value)]"}
  instructions

  {`push-local
   (fn [state name value]
     (let [var-reg (inc (:max-reg state))
           in-reg (fetch-first (:stack state))]
       [(->
         state
         (update-in [:variables value]  #(conj % var-reg))
         (update :stack rest)
         (assoc :max-reg var-reg))
        (instr `copy {:in [in-reg] :out [var-reg]})]))

   `pop-local
   (fn [state name value]
     [(update-in state [:variables value] rest)
      (instr `nop {})])

   `get-var
   (fn [state name value]
     (if (get (:variables state) value)
       (let [out-reg (inc (:max-reg state))
             in-reg (fetch-first (get-in state [:variables value]))]
             [(->
               state
               (update :stack #(conj % out-reg))
               (assoc :max-reg out-reg))
              (instr `copy {:in [in-reg] :out [out-reg]})])
       ((std-instruction-c :in 0 :out 1) state `get-global value)))

   `call
   (std-instruction (fn [value]
                      {:in-count (inc value) :out-count 1}))

   `const (std-instruction-c :in 0 :out 1)
   `jump-if (std-instruction-c :in 0 :out 0)
   `nop (std-instruction-c :in 0 :out 0)
   `negate (std-instruction-c :in 1 :out 1)
   `discard (std-instruction-c :in 1 :out 0)
   `get-global (std-instruction-c :in 0 :out 1)
   `func (std-instruction-c :in 0 :out 1)
   `end (std-instruction-c :in 1 :out 0)})

(defn stack-to-register-step [state vertex]
   (let [item (graph/get-value vertex)
         name (first item)
         value (second item)]
     (assert item (format "item is %s" item))
;     (println name value state)
     (let [[new-state new-item]
           ((fetch instructions name) state name value)]
       (assert new-item (format "fn for %s returned %s" name new-item))
       [new-state (graph/with-value vertex new-item)])))

(defn stack-to-register [vertex]
  (graph/walk-with-state
    {:variables {}, :max-reg 0, :stack nil}
    vertex
    stack-to-register-step))
