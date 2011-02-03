package com.nerderg.rules

class RulesEngineController {

    def rulesEngineService
    static allowedMethods = [fire: ["POST", "GET"]]

    def fire = {
        def ruleSet = params.ruleSet
        def facts = params.facts
        if (ruleSet && facts) {
            try {
                render rulesEngineService.applyRules(ruleSet, facts)
            } catch (e) {
                log.error e
                e.printStackTrace()
                response.status = 500
                render "Error: '$e'"
            }
        } else {
            response.status = 400
            render "Error in request: '" + (ruleSet ? "" : " ruleSet is not set. ") +
                    (facts ? "" : " facts not set.") + "'"
        }
    }
}
