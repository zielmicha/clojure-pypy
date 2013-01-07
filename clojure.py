#!/usr/bin/env pypy

import clpy.tests

def entry_point(argv):
    clpy.tests.run()
    print 'Hello from RPython! If you are seeing this, all tests passed!'
    return 0

def target(*args):
    return entry_point, None

if __name__ == '__main__':
    import sys
    sys.exit(entry_point(sys.argv))
