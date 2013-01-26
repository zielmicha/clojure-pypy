'''
Clojure to bytecode compiler.

Overview
-----------

clpy/compiler/compiler.clj - takes Clojure sexprs, macroexpands them and
translates into stack-based IR

clpy/compiler/transformer.clj - takes IR, applies transformations such as
finding which variables are local, outputs register-based IR

clpy/serializer.clj - takes register-based IR and outputs bytecode

clpy/serializer.py - loads bytecode inside PyPy VM
'''
