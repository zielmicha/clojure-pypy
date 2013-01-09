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
        #raise NotImplementedError
        return -1

    def __repr__(self):
        if self.repr() != Root.repr(self):
            return 'Root[%s]' % self.repr()
        else:
            return object.__repr__(self)

class Sequence(Root):
    pass
