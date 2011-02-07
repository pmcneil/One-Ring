package com.nerderg.rules

class RuleSetController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [ruleSetInstanceList: RuleSet.list(params), ruleSetInstanceTotal: RuleSet.count()]
    }

    def create = {
        def ruleSetInstance = new RuleSet()
        ruleSetInstance.properties = params
        return [ruleSetInstance: ruleSetInstance]
    }

    def save = {
        def ruleSetInstance = new RuleSet(params)
        def engine = new RulesEngine()
        def testResults = engine.testRuleset(ruleSetInstance)

        if (testResults.empty && ruleSetInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'ruleSet.label', default: 'RuleSet'), ruleSetInstance.id])}"
            redirect(action: "show", id: ruleSetInstance.id)
        }
        else {
            if (!testResults.empty) {
                flash.message = "Tests failed, rules not saved $testResults"
            }
            render(view: "create", model: [ruleSetInstance: ruleSetInstance])
        }
    }

    def show = {
        def ruleSetInstance = RuleSet.get(params.id)
        if (ruleSetInstance) {
            [ruleSetInstance: ruleSetInstance]
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ruleSet.label', default: 'RuleSet'), params.id])}"
            redirect(action: "list")
        }
    }

    def edit = {
        def ruleSetInstance = RuleSet.get(params.id)
        if (ruleSetInstance) {
            return [ruleSetInstance: ruleSetInstance]
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ruleSet.label', default: 'RuleSet'), params.id])}"
            redirect(action: "list")
        }
    }

    def update = {
        def ruleSetInstance = RuleSet.get(params.id)
        if (ruleSetInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (ruleSetInstance.version > version) {

                    ruleSetInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'ruleSet.label', default: 'RuleSet')] as Object[], "Another user has updated this RuleSet while you were editing")
                    render(view: "edit", model: [ruleSetInstance: ruleSetInstance])
                    return
                }
            }
            ruleSetInstance.properties = params
            def engine = new RulesEngine()
            def testResults = engine.testRuleset(ruleSetInstance)

            if (testResults.empty && !ruleSetInstance.hasErrors() && ruleSetInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'ruleSet.label', default: 'RuleSet'), ruleSetInstance.id])}"
                redirect(action: "show", id: ruleSetInstance.id)
            }
            else {
                if (!testResults.empty) {
                    flash.message = "Tests failed, rules not saved $testResults"
                }
                render(view: "edit", model: [ruleSetInstance: ruleSetInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ruleSet.label', default: 'RuleSet'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def ruleSetInstance = RuleSet.get(params.id)
        if (ruleSetInstance) {
            try {
                ruleSetInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'ruleSet.label', default: 'RuleSet'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'ruleSet.label', default: 'RuleSet'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ruleSet.label', default: 'RuleSet'), params.id])}"
            redirect(action: "list")
        }
    }
}
