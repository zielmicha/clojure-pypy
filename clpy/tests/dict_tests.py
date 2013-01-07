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
