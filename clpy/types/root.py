'''
Parent class for all user objects and some helper interfaces.
'''

class Root(object):
    '''
    Parent class for all user objects.
    '''

    def repr(self):
        '''
        Return app-level representation of object for debugging.
        '''
        return '<object %s>' % 'of unknown type'

    def set_item(self, key, val):
        '''
        Set dictionary item or vector item (via wrapped int).
        '''
        raise NotImplementedError

    def get_item(self, key):
        '''
        Get dictionary item or vector item (via wrapped int).
        '''
        raise NotImplementedError

    def eq(self, other):
        return self is other

    def hash(self):
        raise NotImplementedError

    def to_int(self):
        raise NotImplementedError

    def assoc(self, key, val):
        raise NotImplementedError

    def __repr__(self):
        if self.repr() != Root.repr(self):
            return 'Root[%s]' % self.repr()
        else:
            return object.__repr__(self)

class Sequence(Root):
    def get_item(self, key):
        return self.get_at(key.to_int())

    def assoc(self, key, val):
        return self.assoc(key.to_int(), val)
