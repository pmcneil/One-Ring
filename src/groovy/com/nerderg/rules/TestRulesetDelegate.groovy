package com.nerderg.rules

/**
 * User: pmcneil
 * Date: 4/02/11
 *
 */
class TestRulesetDelegate {

    def tests

    def require(List params) {
    }

    def rule(String name, Closure cl) {
    }

    def test(Map map, Closure testClosure) {
        def expect = [:]
        tests.add([input: map, expect: expect])
        def delegate = new TestDelegate()
        delegate.metaClass.methodMissing = {String methodName, args ->
            expect.put(methodName, args[0])
        }
        testClosure.delegate = delegate
        testClosure()
    }
}
