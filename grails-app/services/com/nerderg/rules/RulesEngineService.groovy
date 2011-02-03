package com.nerderg.rules

import grails.converters.deep.JSON
import grails.converters.deep.XML

class RulesEngineService {

    static transactional = false
    static final expose = ['cxf']

    String applyRules(String ruleSet, String facts) {
        try {
            boolean xml = facts.trim().startsWith("<")
            RulesEngine engine = new RulesEngine()
            log.debug "The raw facts are $facts"
            def theFacts = xml ? XML.parse(facts) : JSON.parse(facts)
//            log.debug "The decoded facts are $theFacts"
            engine.process(ruleSet, theFacts)
            log.debug "The facts after processing are $theFacts"
            return xml ? theFacts as XML : theFacts as JSON
        } catch (Exception e) {
            e.printStackTrace()
            throw e
        }
    }
}
