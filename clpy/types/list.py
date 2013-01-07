from clpy.types.root import Root

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
