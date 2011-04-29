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

class RulesEngineControllerTests extends ControllerUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testFireJson1() {
        mockLogging(RulesEngineService, true)
        mockLogging(RulesEngine, true)
        def ruleSet = new RuleSet(name: "Means Test", ruleSet: """
        ruleset("Means Test") {
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
        }""")
        mockDomain(RuleSet, [ruleSet])
        controller.rulesEngineService = new RulesEngineService()
        controller.params.ruleSet = "Means Test"
        controller.params.facts = "[{id: 1, income: 900, expenses: 501},{id: 2, income: 2000, expenses: 600}]"
        controller.fire()
        println controller.response.contentAsString
        //this may be fragile if the order changes
        def expected = '[{"nett_income":399,"id":1,"incomeTest":"passed","income":900,"expenses":501},{"nett_income":1400,"id":2,"incomeTest":"failed","income":2000,"expenses":600}]'
        assert controller.response.contentAsString == expected
    }

    void testFireJson2() {
        mockLogging(RulesEngineService, true)
        mockLogging(RulesEngine, true)
        def ruleSet = new RuleSet(name: "Means Test", ruleSet: """ruleset("Means Test") {
            require(['income', 'expenses'])
            rule("nett income") {
                when {
                    income == "heaps"
                }
                then {
                    incomeTest = 'passed'
                }
                otherwise {
                    incomeTest = 'failed'
                }
            }
        }""")
        mockDomain(RuleSet, [ruleSet])
        controller.rulesEngineService = new RulesEngineService()
        controller.params.ruleSet = "Means Test"
        controller.params.facts = """[{id: 1, income: lots, expenses: 501},{id: 2, income: 2000, expenses: 600}]"""
        controller.fire()
        println controller.response.contentAsString
        def expected = '[{"id":1,"incomeTest":"failed","income":"lots","expenses":501},{"id":2,"incomeTest":"failed","income":2000,"expenses":600}]'
        assert controller.response.contentAsString == expected
    }

    void testFireJson3() {
        mockLogging(RulesEngineService, true)
        mockLogging(RulesEngine, true)
        def ruleSet = new RuleSet(name: "Means Test", ruleSet: """ruleset("Means Test") {
            require(['income', 'expenses'])
            rule("nett income") {
                when {
                    income == "23heaps"
                }
                then {
                    incomeTest = 'passed'
                }
                otherwise {
                    incomeTest = 'failed'
                }
            }
        }""")
        mockDomain(RuleSet, [ruleSet])
        controller.rulesEngineService = new RulesEngineService()
        controller.params.ruleSet = "Means Test"
        controller.params.facts = """[{id: 1, income: 23heaps, expenses: 501},{id: 2, income: 2000, expenses: 600}]"""
        controller.fire()
        println controller.response.contentAsString
        def expected = '[{"id":1,"incomeTest":"passed","income":"23heaps","expenses":501},{"id":2,"incomeTest":"failed","income":2000,"expenses":600}]'
        assert controller.response.contentAsString == expected
    }

    void testFireXml1() {
        mockLogging(RulesEngineService, true)
        mockLogging(RulesEngine, true)
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
            test(income: 900, expenses: 501) {
                incomeTest 'passed'
                nett_income 399
            }
        }""")
        mockDomain(RuleSet, [ruleSet])
        controller.rulesEngineService = new RulesEngineService()
        controller.params.ruleSet = "Means Test"
        controller.params.facts = """<list><map><entry key="income">900</entry><entry key="expenses">300</entry></map></list>"""
        controller.fire()
        println controller.response.contentAsString
        def expected = '<?xml version="1.0" encoding="UTF-8"?><list><map><entry key="income">900</entry><entry key="expenses">300</entry><entry key="nett_income">600</entry><entry key="incomeTest">failed</entry></map></list>'
        assert controller.response.contentAsString == expected
    }

    void testFireXml2() {
        mockLogging(RulesEngineService, true)
        mockLogging(RulesEngine, true)
        def ruleSet = new RuleSet(name: "Means Test", ruleSet: """ruleset("Means Test") {
            require(['income', 'expenses'])
            rule("nett income") {
                when {
                    income == "heaps"
                }
                then {
                    incomeTest = 'passed'
                }
                otherwise {
                    incomeTest = 'failed'
                }
            }
        }""")
        mockDomain(RuleSet, [ruleSet])
        controller.rulesEngineService = new RulesEngineService()
        controller.params.ruleSet = "Means Test"
        controller.params.facts = """<list><map><entry key="income">lots</entry><entry key="expenses">300</entry></map></list>"""
        controller.fire()
        println controller.response.contentAsString
        def expected = '<?xml version="1.0" encoding="UTF-8"?><list><map><entry key="income">lots</entry><entry key="expenses">300</entry><entry key="incomeTest">failed</entry></map></list>'
        assert controller.response.contentAsString == expected
    }

    void testFireXml3() {
        mockLogging(RulesEngineService, true)
        mockLogging(RulesEngine, true)
        def ruleSet = new RuleSet(name: "Means Test", ruleSet: """ruleset("Means Test") {
            require(['income', 'expenses'])
            rule("nett income") {
                when {
                    income == "23heaps"
                }
                then {
                    incomeTest = 'passed'
                }
                otherwise {
                    incomeTest = 'failed'
                }
            }
        }""")
        mockDomain(RuleSet, [ruleSet])
        controller.rulesEngineService = new RulesEngineService()
        controller.params.ruleSet = "Means Test"
        controller.params.facts = """<list><map><entry key="income">23heaps</entry><entry key="expenses">300</entry></map></list>"""
        controller.fire()
        println controller.response.contentAsString
        def expected = '<?xml version="1.0" encoding="UTF-8"?><list><map><entry key="income">23heaps</entry><entry key="expenses">300</entry><entry key="incomeTest">passed</entry></map></list>'
        assert controller.response.contentAsString == expected
    }

}
