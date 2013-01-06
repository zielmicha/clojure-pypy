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
        return '<object %s>' % 'whatever'
