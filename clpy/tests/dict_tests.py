from clpy.types.root import Root
from clpy.types.dict import PersistentHashTrie
from clpy.types.string import String
from clpy.space import Space

class HashAndEqSimulator(Root):
    def __init__(self, hash, eqid):
        self._hash = hash
        self._eqid = eqid

    def eq(self, other):
        if isinstance(other, HashAndEqSimulator):
            return other._eqid == self._eqid
        else:
            return False

    def hash(self):
        return self._hash

def test_hashtrie_like_mutable():
    space = Space()
    t = PersistentHashTrie(space)
    t._set_item(String("foobar"), String("foobarval"))
    t._set_item(String("foobar2"), String("foobarval2"))
    assert space.eq( t.get_item(String("foobar")), String("foobarval") )
    assert space.eq( t.get_item(String("foobar2")), String("foobarval2") )
    assert len(t.keys()) == 2
    assert space.eq(t.keys()[0], String("foobar")) or \
        space.eq(t.keys()[1], String("foobar"))
    assert space.eq(t.keys()[0], String("foobar2")) or \
        space.eq(t.keys()[1], String("foobar2"))

def test_hashtrie_like_mutable_with_many():
    space = Space()
    t = PersistentHashTrie(space)
    items = [ String("foo%d" % i) for i in xrange(200) ]
    for item in items:
        t._set_item(item, item)

    for item in items:
        assert space.eq(t.get_item(item), item)

    for item in items:
        t._set_item(item, item)
        assert space.eq(t.get_item(item), item)

    for item in items:
        assert space.eq(t.get_item(item), item)

    assert len(t.keys()) == len(items)

def skip_test_hashtrie_like_mutable_collisions():
    space = Space()
    t = PersistentHashTrie(space)
    # two different objects with same hash
    a = HashAndEqSimulator(100, 1)
    b = HashAndEqSimulator(100, 2)
    t._set_item(a, String("a"))
    t._set_item(b, String("b"))
    assert space.eq( t.get_item(a), String("a") )
    assert space.eq( t.get_item(b), String("b") )
    assert len(t.keys()) == 2

def test_persistent_hashtrie():
    space = Space()
    t = PersistentHashTrie(space)
    t1 = t.assoc(String("a"), String("b"))
    assert len(t1.keys()) == 1
    assert space.eq( t1.keys()[0], String("a") )
    assert space.eq( t1.get_item(String("a")), String("b") )
    t2 = t1.assoc(String("b"), String("c"))
    assert len(t2.keys()) == 2

def test_persistent_hashtrie_with_many():
    space = Space()
    t = PersistentHashTrie(space)
    items = [ String("foo%d" % i) for i in xrange(200) ]
    for item in items:
        t = t.assoc(item, item)

    assert len(t.keys()) == len(items)
    for item in items:
        assert space.eq(t.get_item(item), item)

    for item in items:
        t = t.assoc(item, item)
        assert space.eq(t.get_item(item), item)

    for item in items:
        assert space.eq(t.get_item(item), item)

    assert len(t.keys()) == len(items)

def skip_test_persistent_hashtrie_collisions():
    space = Space()
    t0 = PersistentHashTrie(space)
    # two different objects with same hash
    a = HashAndEqSimulator(100, 1)
    b = HashAndEqSimulator(100, 2)
    t1 = t0.assoc(a, String("a"))
    t = t1.assoc(b, String("b"))
    assert space.eq( t.get_item(a), String("a") )
    assert space.eq( t.get_item(b), String("b") )
    assert len(t.keys()) == 2
