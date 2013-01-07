from clpy.types.root import Root

class Dict(Root):
    '''
    Base implementation of dictionary to be extended by concrete implementation.
    '''

class PersistentHashTrie(Dict):
    '''
    An implementation of persistent hash trie.
    Currently rather naive - the trie is binary and always has depth == 32.

    Name explains the algorithm - it's hash table that uses
    trie keyed by hashes instead of arrays indexed with modular
    arthmetics.
    '''
    def __init__(self, space):
        self.root = _TrieNode()
        self.space = space

    def _set_item(self, key, val):
        hash = self.space.hash(key)
        found = self._trie_find_or_create_inplace(hash)
        self._entry_set(found, key, val)

    def assoc(self, key, val):
        hash = self.space.hash(key)
        new_root, new_node = self._trie_find_or_create_copying(hash)
        self._entry_set(new_node, key, val)
        new = PersistentHashTrie(self.space)
        new.root = new_root
        return new

    def get_item(self, key):
        hash = self.space.hash(key)
        found = self._trie_find(hash)
        if not found:
            raise KeyError()
        return self._entry_get(found, key)

    def _dump(self):
        "NOT_RPYTHON"
        def dump_node(n):
            if not n: return 'nil'
            return '(%s %s %s)' % (dump_entry(n.value), dump_node(n.node0),
                                   dump_node(n.node1))
        def dump_entry(n):
            s = ''
            while n:
                s += '%s:%s' % (n.key, n.value)
                n = n.next
            return '[%s]' % s
        return dump_node(self.root)

    def _trie_find(self, hash):
        '''
        Return _HashEntry for `hash`.
        '''
        node = self.root
        for i in xrange(32):
            node = node.node0 if (hash & 1) else node.node1
            if not node:
                return None
            hash = hash >> 1
        return node.value

    def _trie_find_or_create_copying(self, hash):
        '''
        Return new root _TrieNode and _TrieNode holding _HashEntry for `hash`,
        creating it if needed, keeping `self` untouched.
        Time and memory complexity O(1).
        '''
        node = self.root
        new_node = new_root = _TrieNode()
        for i in xrange(32):
            if not node:
                # node is not in original trie
                # pretend that it was
                node = _TrieNode()
            new_node.value = node.value # not really needed as values
            # of all nodes with depth != 32 are not used
            if (hash & 1):
                # copy node0, keep node1
                new_node.node1 = node.node1
                new_node.node0 = _TrieNode()
                new_node = new_node.node0
                node = node.node0
            else:
                # copy node1, keep node0
                new_node.node0 = node.node0
                new_node.node1 = _TrieNode()
                new_node = new_node.node1
                node = node.node1
            hash = hash >> 1
        if node:
            new_node.value = node.value
        return new_root, new_node

    def _trie_find_or_create_inplace(self, hash):
        '''
        Return _TrieNode holding _HashEntry for `hash`, creating it
        if needed.
        '''
        node = self.root
        for i in xrange(32):
            node_n = node.node0 if (hash & 1) else node.node1
            if not node_n:
                if (hash & 1):
                    node_n = node.node0 = _TrieNode()
                else:
                    node_n = node.node1 = _TrieNode()
            node = node_n
            hash = hash >> 1
        return node

    def _entry_get(self, entry, key):
        while entry:
            if self.space.eq(key, entry.key):
                return entry.value
            entry = entry.next
        raise KeyError()

    def _entry_set(self, trie_node, key, value):
        if not trie_node.value:
            next_entry = trie_node.value = _HashEntry(key)
        else:
            entry = trie_node.value
            while True:
                if self.space.eq(key, entry.key):
                    next_entry = entry
                    break
                if not entry.next:
                    next_entry = entry.next = _HashEntry(key)
                    break
                entry = entry.next

        next_entry.value = value

    def keys(self):
        l = []
        self._get_keys(self.root, l)
        return l

    def _get_keys(self, node, output):
        if node.value:
            entry = node.value
            while entry:
                output.append(entry.key)
                entry = entry.next
        if node.node0:
            self._get_keys(node.node0, output)
        if node.node1:
            self._get_keys(node.node1, output)

class _TrieNode(object):
    def __init__(self):
        self.node0 = None
        self.node1 = None
        self.value = None

class _HashEntry(object):
    def __init__(self, key):
        self.key = key
        self.value = None
        self.next = None
