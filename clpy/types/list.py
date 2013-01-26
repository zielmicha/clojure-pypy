from clpy.types.root import Root

def make_list(space, l):
    v = nil
    for item in l:
        v = List(item, v)
    return v

class List(Root):
    '''
    Immutable list. Or rather cons cell.
    '''
    def __init__(self, head, tail):
        self.head = head
        self.tail = tail

    def rest(self):
        return self.tail

    def first(self):
        return self.head

    def repr(self):
        return 'List(%s)' % ' '.join([ str(item.repr()) for item in self.to_list() ])

    def to_list(self):
        l = self
        r = []
        while l is not nil:
            r.append(l.head)
            l = l.tail
        return r

nil = List(None, None)
