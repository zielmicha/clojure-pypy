from clpy.types.root import Root

class String(Root):
    def __init__(self, value):
        self.value = value

    def repr(self):
        return '"%s"' % self.value

    def hash(self):
        h = 0
        for ch in self.value:
            h *= 17
            h += ord(ch)
        return h

    def eq(self, other):
        if isinstance(other, String):
            return self.value == other.value
        else:
            return False
