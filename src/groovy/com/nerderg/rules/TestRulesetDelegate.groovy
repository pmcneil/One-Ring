/* Copyright 2010, 2011 Peter McNeil

This file is part of One Ring.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy
of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
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
