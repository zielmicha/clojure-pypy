from clpy.types.root import Root

class Dict(Root):
    '''
    Base implementation of dictionary to be extended by concrete implementation.
    '''

class PersistentHashTrie(Dict):
    '''
    An implementation of persistent hash trie.
    Currently rather naive - the trie is binary and always has depth == 32.
    '''
    def __init__(self, space):
        self.root = _TrieNode()
        self.space = space

    def _set_item(self, key, val):
        hash = self.space.hash(key)
        found = self._trie_find_or_create(hash)
        self._entry_set(found, key, val)

    def assoc(self, key, val):
        hash = self.space.hash(key)

    def get_item(self, key):
        hash = self.space.hash(key)
        found = self._trie_find(hash)
        if not found:
            raise KeyError()
        return self._entry_get(found, key)

    def _trie_find(self, hash):
        node = self.root
        for i in xrange(32):
            node = node.node0 if (hash & 1) else node.node1
            if not node:
                return None
            hash = hash >> 1
        return node.value

    def _trie_find_or_create(self, hash):
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
