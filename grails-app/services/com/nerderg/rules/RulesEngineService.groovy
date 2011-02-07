package com.nerderg.rules

import grails.converters.deep.JSON
import grails.converters.XML

class RulesEngineService {

    static transactional = false
    static final expose = ['cxf']

    String applyRules(String ruleSet, String facts) {
        try {
            boolean xml = facts.trim().startsWith("<")
            RulesEngine engine = new RulesEngine()
            log.debug "The raw facts are $facts"
            def theFacts = xml ? parseXmlFacts(facts) : JSON.parse(facts)
            log.debug "The decoded facts are $theFacts"
            engine.process(ruleSet, theFacts)
            log.debug "The facts after processing are $theFacts"
            def result = xml ? theFacts as XML : theFacts as JSON
            return result
        } catch (Exception e) {
            e.printStackTrace()
            throw e
        }
    }

    private def parseXmlFacts(facts) {
        def rootNode = XML.parse(facts)
        def theFacts = []
        rootNode.map.each {map ->
            def factmap = [:]
            map.entry.each {entry ->
                def value = entry.text()
                if (value.isNumber()) {
                    value = value.toBigDecimal()
                }
                factmap.put(entry.@key.toString(), value)
            }
            theFacts.add(factmap)
        }
        return theFacts
    }
}
