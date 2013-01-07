'''
Root - Parent class for all user objects.
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
        return id(self)

    def __repr__(self):
        return 'Root[%s]' % self.repr()
