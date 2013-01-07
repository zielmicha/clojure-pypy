from pypy.rlib.objectmodel import r_dict

from clpy.types.root import Root

class MutableDict(Root):
    '''
    Mutable dictionary based on PyPy's implementation.
    '''
    def __init__(self, space):
        self.container = r_dict(space.eq, space.hash)

    def repr(self):
        return 'MutableDict{%s}' % ', '.join([
            '%s: %s' % (k.repr(), v.repr())
            for k, v in self.container.items() ])

    def set_item(self, key, val):
        self.container[key] = val

    def get_item(self, key):
        return self.container[key]
