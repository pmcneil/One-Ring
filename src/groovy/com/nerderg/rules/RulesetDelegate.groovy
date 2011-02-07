package com.nerderg.rules

/**
 * User: pmcneil
 * Date: 28/01/11
 *
 */
class RulesetDelegate {

    String name
    def fact
    def abort

    def require(List params) {
        params.each {
            if (fact[it] == null) {
                abort = 'abort'
                fact.error = (fact.error ? "${fact.error} " : "") + "Fact $it not found."
            }
        }
    }

    def rule(String name, Closure cl) {
        if ('abort' != abort) {
            cl.delegate = new RuleDelegate(name: name, fact: fact)
            cl.resolveStrategy = Closure.DELEGATE_FIRST
            println "runing ${this.name} rule $name on $fact"
            abort = cl()
        }
    }

    def test(Map map, Closure testClosure) {
        //do nothing
    }
}
