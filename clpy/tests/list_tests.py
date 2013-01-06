from clpy.types.list import *
from clpy.types.root import *

class Foobar(Root):
    def repr(self):
        return 'an foobar object'

def test_typing():
    assert List(List(nil, nil), nil)
    assert List(Foobar(), nil)
    assert List(Foobar(), List(Foobar(), nil))

def test_iterate():
    a = Foobar()
    b = Foobar()
    l = List(a, List(b, nil))
    assert l.to_list() == [a, b]

def test_repr():
    a = Foobar()
    b = Foobar()
    l = List(a, List(b, nil))
    assert l.repr() == 'List(an foobar object an foobar object)'
