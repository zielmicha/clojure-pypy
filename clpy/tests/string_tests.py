from clpy.types.string import String
from clpy.space import Space

def test_string():
    space = Space()
    assert space.hash(String('a')) == space.hash(String('a'))
    assert space.eq(String('a'),  String('a'))
    assert not space.eq(String('a'), String('b'))
