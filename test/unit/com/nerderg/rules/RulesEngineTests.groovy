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
    @Override
    protected void setUp() {
        super.setUp()
        mockLogging(RulesEngine, true)
        mockLogging(RulesetDelegate, true)
        mockLogging(RuleDelegate, true)
    }


    void testProcessRulesScript() {
        List<RulesetDelegate> ruleSets = RulesEngine.processRules(rule)
        assert ruleSets.size() == 2
        assert ruleSets[0].name == "Means Test"
        assert ruleSets[0].rules.size() == 3
        assert ruleSets[0].required == ['income', 'expenses']
        assert ruleSets[0].tests[0].input == [income: 900, expenses: 501]
        ruleSets.each { ruleSet ->
            List fails = RulesEngine.testRuleset(ruleSet)
            assert fails.isEmpty()
        }
    }

    private String rule = """ ruleset("Means Test") {
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
        nett_income 399
    }
}
ruleset("milkshake") {
    require(["singer"])
    rule("singer is Kelis") {
        when { singer =~ /(?i)Kelis/ }
        then { boys = 'In the yard' }
        otherwise { boys = 'not in the yard' }
    }
    test(singer: 'Kelis') { boys 'In the yard' }
    test(singer: 'kElis') { boys 'In the yard' }
    test(singer: 'Snoop Dog') { boys 'not in the yard' }
} """

    void testRunRules() {
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

        List<RulesetDelegate> ruleSets = RulesEngine.processRules(dslScript)
        assert ruleSets
        assert ruleSets.size() == 1

        RulesetDelegate ruleSet = ruleSets[0]
        assert !ruleSet.abortOnFail

        def fact = [income: 900, expenses: 600]

        long start = System.currentTimeMillis()
        ruleSet.runRules(fact)
        println "Rules run in " + (System.currentTimeMillis() - start) + "ms"

        assert fact.nett_income == 300
        assert fact.incomeTest == 'passed'
        assert fact.incomeTest2
        assert fact.incomeTest3 == 'poor bugger'

        fact = [income: 900, expenses: 400]

        start = System.currentTimeMillis()
        ruleSet.runRules(fact)
        println "Rules run in " + (System.currentTimeMillis() - start) + "ms"

        assert fact.nett_income == 500
        assert fact.incomeTest == 'failed'
        assert !fact.incomeTest2
        assert fact.incomeTest3 == 'rich bugger'

        fact = [expenses: 900]

        start = System.currentTimeMillis()
        ruleSet.runRules(fact)
        println "Rules run in " + (System.currentTimeMillis() - start) + "ms"

        println fact
        assert fact.error
        assert fact.error == "Fact income not found."

        fact = [:]

        start = System.currentTimeMillis()
        ruleSet.runRules(fact)
        println "Rules run in " + (System.currentTimeMillis() - start) + "ms"

        println fact
        assert fact.error
        assert fact.error == "Fact income not found. Fact expenses not found."
    }

    void testRunRulesAbortOnFail() {
        def dslScript = """ruleset("Means Test") {
            require(['income', 'expenses'])

            abortOnFail = true

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

        List<RulesetDelegate> ruleSets = RulesEngine.processRules(dslScript)
        assert ruleSets
        assert ruleSets.size() == 1

        RulesetDelegate ruleSet = ruleSets[0]
        assert ruleSet
        assert ruleSet.abortOnFail

        def fact = [income: 900, expenses: 600]

        ruleSet.runRules(fact)

        assert fact.nett_income == 300
        assert fact.incomeTest == 'passed'
        assert fact.incomeTest2
        assert fact.incomeTest3 == 'poor bugger'

        fact = [income: 900, expenses: 400]

        ruleSet.runRules(fact)

        assert fact.nett_income == 500
        assert fact.incomeTest == 'failed'
        assert !fact.containsKey('incomeTest2')
        assert !fact.containsKey('incomeTest3')
    }

    void testTestRulesDsl() {
        mockLogging(RulesEngine, true)
        mockLogging(RulesetDelegate, true)
        mockLogging(RuleDelegate, true)

        String ruleDsl = """ruleset("Means Test") {
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
        }"""

        List<RulesetDelegate> ruleSets = RulesEngine.processRules(ruleDsl)
        assert ruleSets
        assert ruleSets.size() == 1

        RulesetDelegate ruleSet = ruleSets[0]

        List<String> fails = RulesEngine.testRuleset(ruleSet)
        assert !fails.empty
        assert fails[0].startsWith("assert facts[name]")

        ruleDsl = """ruleset("Means Test") {
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

        ruleSets = RulesEngine.processRules(ruleDsl)
        assert ruleSets
        assert ruleSets.size() == 1
        ruleSet = ruleSets[0]

        fails = RulesEngine.testRuleset(ruleSet)
        assert fails.empty

        ruleDsl = """
        ruleset('test') {

            require(['value'])

            rule('quantity within range') {

                when {
                    value < 1 || value > 10
                }

                then {
                    failed = true
                    message = ["invalid quantity"]
                }

                otherwise {
                    failed = false
                    message = []
                }

            }
            test(value: 0) {
                failed true
                message (["invalid quantity"])
            }
        }
        ruleset('checkRef') {
            require(['ref'])
            rule('should be able to reference map in ref')  {
                when {
                    ref.value == 23
                }
                then {
                    ref.yes = true
                }
                otherwise {
                    ref.yes = false
                }
            }

            test(ref: [value: 23]) {
                ref([value: 23,yes: true])
            }
            test(ref: [value: 2]) {
                ref([value: 2, yes: false])
            }
    }
        """
        ruleSets = RulesEngine.processRules(ruleDsl)
        assert ruleSets
        assert ruleSets.size() == 2

        ruleSets.each { rs ->
            fails = RulesEngine.testRuleset(rs)
            assert fails.empty
        }
    }


    void testBenchmarkRunRules() {
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

        List<RulesetDelegate> ruleSets = RulesEngine.processRules(dslScript)
        assert ruleSets
        assert ruleSets.size() == 1

        RulesetDelegate ruleSet = ruleSets[0]
        assert ruleSet
        assert !ruleSet.abortOnFail

        def fact = [income: 900, expenses: 600]

        long start = System.currentTimeMillis()
        1000.times {
            ruleSet.runRules(fact)
        }
        println "Rules run 1000 times in avg " + (System.currentTimeMillis() - start) / 1000 + "ms each"
    }
}
