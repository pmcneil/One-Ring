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
 * Date: 28/01/11
 *
 */
class RulesetDelegate {

    String name
    List<Closure> rules = []
    List required = []
    List tests = []
    boolean abortOnFail = false

    def require(List params) {
        required = params
    }

    boolean checkRequired(Map fact) {
        boolean ok = true
        required.each {
            if (fact[it] == null) {
                ok = false
                fact.error = (fact.error ? "${fact.error} " : "") + "Fact $it not found."
            }
        }
        return ok
    }

    def rule(String name, Closure cl) {
        cl.delegate = new RuleDelegate(name: name)
        cl.resolveStrategy = Closure.DELEGATE_FIRST
//        println "ruleSet ${this.name} rule $name created"
        rules.add(cl)
    }

    boolean runRules(Map fact) {
        if (checkRequired(fact)) {
            for (Closure ruleClosure in rules) {
                ruleClosure.fact = fact
                ruleClosure()
                if (!(ruleClosure.result) && abortOnFail) {
                    return false
                }
            }
        } else {
            return false
        }
        return true
    }

    def test(Map map, Closure testClosure) {
        Map expect = [:]
        tests.add([input: map, expect: expect])
        def delegate = new TestDelegate()
        delegate.metaClass.methodMissing = {String methodName, args ->
            expect.put(methodName, args[0])
        }
        testClosure.delegate = delegate
        testClosure()
    }
}
