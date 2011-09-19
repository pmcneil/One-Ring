package com.nerderg.rules

import grails.test.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class RuleSetServiceTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()
        mockLogging(RuleSetService, true)
        mockLogging(RulesEngine, true)
        def mockConfig = new ConfigObject()
        mockConfig.oneRing.rules.directory = 'test/unit/com/nerderg/rules/'
        ConfigurationHolder.config = mockConfig
        def dslScript = """ {
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

        //create 1000 rules in the tmp directory
        File dir = new File('test/unit/com/nerderg/rules/tmp')
        dir.mkdir()
        Integer i = 1
        1000.times {
            String name = "rule$i"
            File ruleFile = new File(dir, "${name}.ruleset")
            ruleFile.write("ruleset(\"$name\") $dslScript")
            i++
        }

    }

    protected void tearDown() {
        super.tearDown()
        File dir = new File('test/unit/com/nerderg/rules/tmp')
        dir.deleteDir()
    }

    void testUpdate() {
        RuleSetService ruleSetService = new RuleSetService()
        ruleSetService.update()
        assert ruleSetService.size() == 1004
        RulesetDelegate ruleSet = ruleSetService.getRuleSet("Means Test")
        assert ruleSet
        assert ruleSet.name == "Means Test"
        assert ruleSet.rules.size() == 3
        assert ruleSet.required == ['income', 'expenses']
        assert ruleSet.tests == [[input: [income: 900, expenses: 501], expect: [incomeTest: 'passed', nett_income: 399]]]

        long start = System.currentTimeMillis()
        ruleSetService.getRuleSetNames().each { name ->
            long startTest = System.currentTimeMillis()
            ruleSet = ruleSetService.getRuleSet(name)
            List fails = RulesEngine.testRuleset(ruleSet)
            println "tested ${ruleSet.name} in " + (System.currentTimeMillis() - startTest) + "ms"
            assert fails.isEmpty()
        }
        println "tested ${ruleSetService.size()} rules in " + (System.currentTimeMillis() - start) + "ms"

    }
}
