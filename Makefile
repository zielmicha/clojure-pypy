PYPY_PATH=$(HOME)/pypy
HOST_PYTHON=pypy
TRANSLATE_OPT=-O0

all: test-c

test: clojure.py
	$(HOST_PYTHON) clojure.py

clojure-c: test clojure.py
	$(HOST_PYTHON) $(PYPY_PATH)/pypy/translator/goal/translate.py $(TRANSLATE_OPT) clojure.py

test-c: clojure-c
	./clojure-c

.PHONY: clojure-c run
