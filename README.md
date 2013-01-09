clojure-pypy
============

Clojure implementation on PyPy.

Rationale
-------------

Clojure on JVM:

* has slow startup time, making it unusable for scripts
* has some annoying misfeatures that could be easily fixed
  with strict mode (see http://zielm.com/?p=4)
* has no tail recursion optimization

And implementation on PyPy will be more flexible than
one on Java, because it will be essentialy interpreter,
not compiler. It will gain comparable speed by using
PyPy's great automatic JIT generation.

Current state
-------------

* basic code structure - done
* implementing necessary types on PyPy - done
* creating compiler - current
* creating interpreter
