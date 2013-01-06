#!/usr/bin/env pypy

import clpy.tests

def entry_point(argv):
    print 'Hello from RPython!'
    clpy.tests.run()
    return 0

def target(*args):
    return entry_point, None

if __name__ == '__main__':
    import sys
    sys.exit(entry_point(sys.argv))
