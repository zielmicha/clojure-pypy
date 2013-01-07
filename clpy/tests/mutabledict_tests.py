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
