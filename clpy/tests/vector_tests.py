from clpy.types.string import String
from clpy.types.vector import PersistentVector
from clpy.space import Space

def test_vector():
    space = Space()
    v = PersistentVector()
    assert v.size() == 0
    v1 = v.with_appended(String('abc'))
    assert v1.size() == 1
    assert space.eq( v1.to_list()[0], String('abc') )
    assert len(v1.to_list()) == 1

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
