from clpy.types.string import String
from clpy.types.number import make_int
from clpy.types.vector import PersistentVector, make_vector
from clpy.space import Space

def test_vector():
    space = Space()
    v = PersistentVector()
    assert v.size() == 0
    v1 = v.with_appended(String('abc'))
    assert v1.size() == 1
    assert space.eq( v1.to_list()[0], String('abc') )
    assert len(v1.to_list()) == 1
    assert space.eq( v1.get_at(0), String('abc') )
    assert space.eq( v1.get_item(space.make_int(0)), String('abc') )

def test_many_append():
    space = Space()
    count = 2000
    v = PersistentVector()
    for i in xrange(count):
        v = v.with_appended(String('%d' % i))
    assert v.size() == count
    for i in xrange(count):
        assert space.eq( v.get_at(i), String('%d' % i) )
    l = v.to_list()
    for i in xrange(count):
        assert space.eq( l[i], String('%d' % i) )

def test_assoc():
    space = Space()
    v = space.make_vector([ String(i) for i in ["a", "b", "c"] ])
    a = v.assoc_at(1, String("fuu"))
    assert a.size() == 3 and space.eq( a.get_at(0), String('a') )
    assert space.eq( a.get_at(1), String('fuu') )
    assert space.eq( a.get_at(2), String('c') )
