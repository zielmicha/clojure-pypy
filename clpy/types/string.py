from clpy.types.root import Root

class String(Root):
    def __init__(self, value):
        self.value = value

    def repr(self):
        return '"%s"' % self.value

    def hash(self):
        return 12 # TODO: real hash

    def eq(self, other):
        if isinstance(other, String):
            return self.value == other.value
        else:
            return False
