from clpy.types.list import make_list
from clpy.types.vector import make_vector

class Space(object):
    def __init__(self):
        pass

    def eq(self, a, b):
        return a.eq(b)

    def hash(self, a):
        return a.hash()

    def make_list(self, v):
        return make_list(self, v)

    def make_vector(self, v):
        return make_vector(self, v)
