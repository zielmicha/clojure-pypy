from clpy.types.root import Sequence

def make_vector(l):
    v = PersistentVector()
    for item in l:
        v = v.with_appended(item)
    return v

class PersistentVector(Sequence):
    '''
    Persistent vector implementation.
    Based on binary trie with constant depth.
    Supports logarithmic append, prepend, lookup and assoc.
    '''
    def __init__(self):
        self._root = _TreeNode()
        self._depth = 0
        self._empty_front = 0
        self._empty_back = 1

    def get_at(self, i):
        capacity = 1 << self._depth
        index = self._empty_front + i
        if i < 0:
            raise IndexError("list index less than 0")
        if index >= capacity - self._empty_back:
            raise IndexError("list index out of range")
        return self._trie_find(index)

    def assoc_at(self, i, val):
        capacity = 1 << self._depth
        index = self._empty_front + i

        if i < 0:
            raise IndexError("list index less than 0")
        if index >= capacity - self._empty_back:
            raise IndexError("list index out of range")

        root, entry = self._trie_find_or_create_copying(index)
        entry.value = val
        new = PersistentVector()
        new._root = root
        new._depth = self._depth
        new._empty_front = self._empty_front
        new._empty_back = self._empty_back
        return new

    def with_appended(self, val):
        capacity = 1 << self._depth
        if self._empty_back == 0:
            # need to make depth bigger
            new_root = _TreeNode()
            new_root.node0 = self._root
            new_root.node1 = None
            vec = PersistentVector()
            vec._depth = self._depth + 1
            vec._root = new_root
            vec._empty_front = self._empty_front
            vec._empty_back = capacity
            return vec.with_appended(val)

        index = capacity - self._empty_back
        root, entry = self._trie_find_or_create_copying(index)
        entry.value = val
        new = PersistentVector()
        new._root = root
        new._depth = self._depth
        new._empty_front = self._empty_front
        new._empty_back = self._empty_back - 1
        return new

    def size(self):
        capacity = 1 << self._depth
        return capacity - self._empty_back - self._empty_front

    def repr(self):
        return 'Vector(%s)' % ', '.join([ str(i) for i in self.to_list() ])

    def to_list(self):
        # TODO: faster!
        l = []
        for i in xrange(0, self.size()):
            l.append(self.get_at(i))
        return l

    def _dump(self, trie):
        if not trie: return '-'
        return '(%s %s %s)' % (self._dump(trie.node0), self._dump(trie.node1), trie.value or '')

    def _trie_find_or_create_copying(self, index):
        node = self._root
        new_node = new_root = _TreeNode()
        for i in xrange(self._depth):
            if not node:
                # node is not in original trie
                # pretend that it was
                node = _TreeNode()
            new_node.value = node.value
            bit = (index >> (self._depth-1)) & 1
            if not bit:
                # copy node0, keep node1
                new_node.node1 = node.node1
                new_node.node0 = _TreeNode()
                new_node = new_node.node0
                node = node.node0
            else:
                # copy node1, keep node0
                new_node.node0 = node.node0
                new_node.node1 = _TreeNode()
                new_node = new_node.node1
                node = node.node1
            index = index << 1
        if node:
            new_node.value = node.value
        return new_root, new_node

    def _trie_find(self, index):
        n = self._root
        for i in xrange(self._depth):
            bit = (index >> (self._depth-1)) & 1
            n = n.node1 if bit else n.node0
            index = index << 1
        return n.value

class _TreeNode(object):
    def __init__(self):
        self.node0 = None
        self.node1 = None
        self.value = None
