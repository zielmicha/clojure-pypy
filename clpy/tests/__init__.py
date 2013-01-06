modules = ['list_tests']

exec 'def run():\n\t' + '\n\t'.join( 'run_%s()' % module for module in modules )
for modname in modules:
    module = __import__('clpy.tests.%s' % modname, {}, {}, [None])
    tests = [ name for name in dir(module) if name.startswith('test_') ]
    globals()[modname] = module
    exec 'def run_%s():\n\t%s' % (modname, '\n\t'.join( '%s.%s()' % (modname, name) for name in tests ) if tests else 'pass')
