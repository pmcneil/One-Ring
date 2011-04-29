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
 * Date: 25/01/11
 *
 */
class RulesEngine {

    static def with(Object target, Closure cl) {
        cl.delegate = target
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        return cl()
    }

    /**
     * process a list of fact maps
     * @param ruleSet - name of the ruleset to process the facts
     * @param facts - a list of fact maps
     * @return facts as modified by the rules
     */
    def process(String ruleSet, facts) {
        def rules = RuleSet.findByName(ruleSet)
        if (!rules) {
            throw new MissingRulesetException("Ruleset $ruleSet not found")
        }
        def dsl = processRules(rules.ruleSet)
        facts.each { fact ->
            dsl.setProperty('fact', fact)
            dsl.run()
        }
        return facts
    }

    List testRuleset(RuleSet rules) {
        def tests = processRuleTests(rules.ruleSet)
        def dsl = processRules(rules.ruleSet)
        def fails = []
        tests.each { testData ->
            dsl.setProperty('fact', testData.input)
            dsl.run()
            testData.expect.each {
                if (testData.input[it.key] != it.value) {
                    fails.add("expected '${it.key}' to be '${it.value}' in test data ${testData.input}")
                }
            }
        }
        return fails
    }

    static def processRuleTests(String dsl) {

        Script dslScript = new GroovyShell().parse(dsl)

        dslScript.metaClass = createEMC(dslScript.class) {
            ExpandoMetaClass emc ->
            emc.tests = []
            emc.ruleset = { name, Closure ruleset ->
                ruleset.delegate = new TestRulesetDelegate(tests: tests)
                ruleset.resolveStrategy = Closure.DELEGATE_FIRST
                ruleset()
            }
        }
        dslScript.run()
        return dslScript.tests
    }

    static Script processRules(String dsl) {

        Script dslScript = new GroovyShell().parse(dsl)

        dslScript.metaClass = createEMC(dslScript.class) {
            ExpandoMetaClass emc ->
            emc.fact = null
            emc.ruleset = { name, Closure ruleset ->
                ruleset.delegate = new RulesetDelegate(name: name, fact: fact)
                ruleset.resolveStrategy = Closure.DELEGATE_FIRST
                ruleset()
            }
        }
        return dslScript
    }

    static ExpandoMetaClass createEMC(Class clazz, Closure cl) {

        ExpandoMetaClass emc = new ExpandoMetaClass(clazz, false)
        cl(emc)
        emc.initialize()
        return emc
    }

}
