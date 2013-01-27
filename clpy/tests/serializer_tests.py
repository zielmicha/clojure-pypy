from clpy.serializer import unserialize
from clpy.space import Space

def test_serialization():
    space = Space()
    zero = unserialize(space, 'I\n0')
    assert space.eq(
        space.make_int(0),
        zero)
    print
