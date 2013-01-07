from clpy.types.mutabledict import MutableDict
from clpy.types.string import String
from clpy.space import Space

def test_repr():
    space = Space()
    d = MutableDict(space)
    assert d.repr() == 'MutableDict{}'
    d.set_item(String('foobar'), String('needle'))
    assert d.repr() == 'MutableDict{"foobar": "needle"}'
    assert space.eq(d.get_item(String('foobar')), String('needle'))

def test_dict_with_many():
    space = Space()
    t = MutableDict(space)
    items = [ String("foo%d" % i) for i in xrange(200) ]
    for item in items:
        t.set_item(item, item)

    for item in items:
        assert space.eq(t.get_item(item), item)

    for item in items:
        t.set_item(item, item)
        assert space.eq(t.get_item(item), item)

    for item in items:
        assert space.eq(t.get_item(item), item)
