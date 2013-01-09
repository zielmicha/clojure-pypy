from clpy.types.root import Root

def make_int(n):
    return Integer(n)

class Integer(Root):
    def __init__(self, val):
        self.val = val

    def to_int(self):
        return self.val
