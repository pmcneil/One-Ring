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

    static Script processRules(String dsl) {

        Script dslScript = new GroovyShell().parse(dsl)

        dslScript.metaClass = createEMC(dslScript.class, {
            ExpandoMetaClass emc ->
            emc.fact = null
            emc.ruleset = { name, Closure cl ->
                cl.delegate = new RulesetDelegate(name: name, fact: fact)
                cl.resolveStrategy = Closure.DELEGATE_FIRST
                cl()
            }
        })
        return dslScript
    }

    static ExpandoMetaClass createEMC(Class clazz, Closure cl) {

        ExpandoMetaClass emc = new ExpandoMetaClass(clazz, false)
        cl(emc)
        emc.initialize()
        return emc
    }

}
