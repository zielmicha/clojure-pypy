from clpy.types.dict import make_dict_from_pairs

def unserialize(space, data):
    u = Unserializer(space, data)
    obj = u.unserialize()
    u.check_eof()
    return obj

class Unserializer:
    def __init__(self, space, data):
        self.stream = Reader(data)
        self.space = space

    def unserialize(self):
        t = self.stream.readline()
        if t == 'L':
            return self.space.make_list(self.unserialize_seq())
        elif t == 'V':
            return self.space.make_vector(self.unserialize_seq())
        elif t == 'M':
            return make_dict_from_pairs(self.space, self.unserialize_seq())
        elif t == 'K':
            raise NotImplementedError
        elif t == 'S':
            raise NotImplementedError
        elif t == 's':
            raise NotImplementedError
        elif t == 'I':
            value = int(self.stream.readline())
            return self.space.make_int(value)
        else:
            raise SerializerError('unknown object type')

    def check_eof(self):
        if self.stream.read(2):
            raise SerializerError#('expected EOF, data found')

    def unserialize_seq(self):
        raise NotImplementedError

class Reader:
    def __init__(self, text):
        self.text = text
        self.i = 0

    def readline(self):
        start = self.i
        while self.i < len(self.text) and self.text[self.i] != '\n':
            self.i += 1
        stop = self.i
        assert stop > 0
        self.i += 1
        return self.text[start : stop]

    def read(self, n):
        start = self.i
        assert start > 0
        self.i += n
        return self.text[start : self.i]

class SerializerError(Exception):
    ''' Exception thrown when [un]serializing '''
    pass
