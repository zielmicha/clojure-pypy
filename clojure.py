#!/usr/bin/env pypy

def entry_point(argv):
    print 'Hello from RPython!'
    return 0

def target(*args):
    return entry_point, None
