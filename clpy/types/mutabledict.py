from pypy.rlib.objectmodel import r_dict

from clpy.types.root import Root
from clpy.types.dict import PersistentHashTrie

class MutableDict(Root):
    '''
    Mutable dictionary that should be based on PyPy's implementation,
    but I can't get it accepted by translator.

    So, for now, it's based on persistent hash trie.
    '''
    def __init__(self, space):
        self.container = PersistentHashTrie(space)

    def repr(self):
        return 'MutableDict{%s}' % ', '.join([
            '%s: %s' % (k.repr(), self.container.get_item(k).repr())
            for k in self.container.keys() ])

    def set_item(self, key, val):
        self.container = self.container.assoc(key, val)

    def get_item(self, key):
        return self.container.get_item(key)
