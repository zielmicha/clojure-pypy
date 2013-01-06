PYPY_PATH=$(HOME)/pypy
HOST_PYTHON=pypy
TRANSLATE_OPT=--view -O0

all: clojure-c

clojure-c: clojure.py
	$(HOST_PYTHON) $(PYPY_PATH)/pypy/translator/goal/translate.py $(TRANSLATE_OPT) clojure.py

.PHONY: clojure-c
