package com.nerderg.rules

import grails.test.*

class RulesEngineControllerTests extends ControllerUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testFire() {
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
        controller.params.facts = """
  <list>
    <map>
      <entry key="income">900</entry>
      <entry key="expenses">300</entry>
    </map>
  </list>
"""
        controller.fire()
        println controller.response.contentAsString
        //this may be fragile if the order changes
//        def expected = '[{"nett_income":399,"id":1,"incomeTest":"passed","income":900,"expenses":501},{"nett_income":1400,"id":2,"incomeTest":"failed","income":2000,"expenses":600}]'
//        assert controller.response.contentAsString == expected

    }
}
