package com.nerderg.rules

/**
 * User: pmcneil
 * Date: 28/01/11
 *
 */
class RuleDelegate {

    String name
    Map fact
    Boolean result = false

    def when(Closure cl) {
        result = RulesEngine.with(fact, cl)
        println "when $name result is $result"
    }

    def then(Closure cl) {
        if (result) {
            println "then $name"
            RulesEngine.with(fact, cl)
        }
    }

    def otherwise(Closure cl) {
        if (!result) {
            println "otherwise $name"
            RulesEngine.with(fact, cl)
        }
    }

    def evaluate(Closure cl) {
        println "eval $name"
        result = RulesEngine.with(fact, cl)
    }
}
