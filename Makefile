PYPY_PATH=$(HOME)/pypy
HOST_PYTHON=pypy
TRANSLATE_OPT=-O0
PY_FILES=clpy/__init__.py \
clpy/types/__init__.py clpy/types/mutabledict.py clpy/types/string.py clpy/types/root.py \
clpy/types/list.py clpy/types/dict.py clpy/types/vector.py \
\
clpy/compiler.py clpy/space.py \
clpy/serializer.py \
\
clpy/tests/list_tests.py clpy/tests/__init__.py clpy/tests/string_tests.py\
clpy/tests/mutabledict_tests.py clpy/tests/dict_tests.py clpy/tests/vector_tests.py \
\
clojure.py

all: test-c

test: clojure.py
	PYTHONPATH=$(PYPY_PATH) $(HOST_PYTHON) clojure.py

clojure-c: $(PY_FILES)
	$(HOST_PYTHON) $(PYPY_PATH)/pypy/translator/goal/translate.py $(TRANSLATE_OPT) clojure.py

test-c: test clojure-c
	./clojure-c

.PHONY: run test-c
