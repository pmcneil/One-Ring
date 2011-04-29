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

import grails.test.*

/**
 * User: pmcneil
 * Date: 25/01/11
 *
 */
class RulesEngineTests extends GrailsUnitTestCase {

    void testProcessRulesDsl() {
        mockLogging(RulesEngine, true)
        mockLogging(RulesetDelegate, true)
        mockLogging(RuleDelegate, true)
        def dslScript = """ruleset("Means Test") {
            require(['income', 'expenses'])
            rule("nett income") {
                when {
                    nett_income = income - expenses
                    nett_income < 400.00
                }
                then {
                    incomeTest = 'passed'
                }
                otherwise {
                    incomeTest = 'failed'
                }
            }

            rule("nett income2") {
                evaluate {
                    incomeTest2 = nett_income < 400.00
                }
            }

            rule("nett income3") {
                if(fact.nett_income > 400) {
                    fact.incomeTest3 = 'rich bugger'
                } else {
                    fact.incomeTest3 = 'poor bugger'
                }
            }

            test(income: 900, expenses: 501) {
                incomeTest 'passed'
            }
        }"""
        def dsl = RulesEngine.processRules(dslScript)
        assert dsl
        def fact = [income: 900, expenses: 600]
        dsl.setProperty('fact', fact)
        dsl.run()
        assert fact.nett_income == 300
        assert fact.incomeTest == 'passed'
        assert fact.incomeTest2
        assert fact.incomeTest3 == 'poor bugger'
        fact.expenses = 400
        fact.remove('nett_income')
        fact.remove('incomeTest')
        dsl.run()
        assert fact.nett_income == 500
        assert fact.incomeTest == 'failed'
        assert !fact.incomeTest2
        assert fact.incomeTest3 == 'rich bugger'
        fact = [expenses: 900]
        dsl.setProperty('fact', fact)
        dsl.run()
        println fact
        assert fact.error
        assert fact.error == "Fact income not found."
        fact = [:]
        dsl.setProperty('fact', fact)
        dsl.run()
        println fact
        assert fact.error
        assert fact.error == "Fact income not found. Fact expenses not found."
    }

    void testTestRulesDsl() {
        mockLogging(RulesEngine, true)
        mockLogging(RulesetDelegate, true)
        mockLogging(RuleDelegate, true)

        def ruleSet = new RuleSet(name: "Means Test", ruleSet: """ruleset("Means Test") {
            require(['income', 'expenses'])
            rule("nett income") {
                when {
                    nett_income = income - expenses
                    nett_income < 400.00
                }
                then {
                    incomeTest = 'passed'
                }
                otherwise {
                    incomeTest = 'failed'
                }
            }
            test(income: 900, expenses: 500) {
                incomeTest 'passed'
            }
        }""")
        mockDomain(RuleSet, [ruleSet])

        RulesEngine engine = new RulesEngine()
        List fails = engine.testRuleset(ruleSet)
        assert !fails.empty
        assert fails[0] == "expected 'incomeTest' to be 'passed' in test data [income:900, expenses:500, nett_income:400, incomeTest:failed]"

        ruleSet.ruleSet = """ruleset("Means Test") {
            require(['income', 'expenses'])
            rule("nett income") {
                when {
                    nett_income = income - expenses
                    nett_income < 400.00
                }
                then {
                    incomeTest = 'passed'
                }
                otherwise {
                    incomeTest = 'failed'
                }
            }
            test(income: 900, expenses: 501) {
                incomeTest 'passed'
                nett_income 399
            }
        }"""
        fails = engine.testRuleset(ruleSet)
        assert fails.empty
    }
}
