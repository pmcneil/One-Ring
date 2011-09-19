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
    static List<Map> process(RulesetDelegate ruleSet, List<Map> facts) {
        facts.each { fact ->
            ruleSet.runRules(fact)
        }
        return facts
    }

    static List testRuleset(RulesetDelegate rules) {
        def fails = []
        rules.tests.each { testData ->
            if (rules.checkRequired(testData.input)) {
                rules.runRules(testData.input)
                testData.expect.each {
                    if (testData.input[it.key] != it.value) {
                        fails.add("expected '${it.key}' to be '${it.value}' in test data ${testData.input}")
                    }
                }
            } else {
                fails.add(testData.input.error)
            }
        }
        return fails
    }

    static List<RulesetDelegate> processRules(String dsl) {
        return processRuleScript(new GroovyShell().parse(dsl))
    }

    static List<RulesetDelegate> processRules(File file) {
        return processRuleScript( new GroovyShell().parse(file))
    }

    static List<RulesetDelegate> processRuleScript(Script dslScript) {

        dslScript.metaClass = createEMC(dslScript.class) {
            ExpandoMetaClass emc ->
            emc.fact = null
            emc.rulesets = []
            emc.ruleset = { name, Closure ruleset ->
                ruleset.delegate = new RulesetDelegate(name: name)
                rulesets.add(ruleset.delegate)
                ruleset.resolveStrategy = Closure.DELEGATE_FIRST
                ruleset()
            }
        }
        dslScript.run()
        return dslScript.rulesets
    }

    static ExpandoMetaClass createEMC(Class clazz, Closure cl) {

        ExpandoMetaClass emc = new ExpandoMetaClass(clazz, false)
        cl(emc)
        emc.initialize()
        return emc
    }

}
