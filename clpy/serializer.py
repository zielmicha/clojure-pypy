
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

class Reader:
    def __init__(self, text):
        self.text = text
        self.i = 0

    def readline(self):
        start = i
        while self.i < len(text) and self.text[self.i] != '\n':
            self.i += 1
        self.i += 1
        return self.text[start : self.i - 1]

    def read(self, n):
        self.i += n
        return self.text[self.i - n : self.i]
