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

import grails.converters.deep.JSON
import grails.converters.XML
import com.nerder.rules.RulesetNotFoundException

class RulesEngineService {

    def ruleSetService
    static transactional = false
    static final expose = ['cxf']
    static exclude = ["fireRules", "parseXmlFacts", "getXMLFacts"]

    String applyRules(String ruleSet, String facts) {
        try {
            boolean xml = facts.trim().startsWith("<")
            log.debug "The raw facts are $facts"
            def theFacts = xml ? parseXmlFacts(facts) : JSON.parse(facts)
            log.debug "The decoded facts are $theFacts"
            def results = fireRules(ruleSet, theFacts)
            log.debug "The facts after processing are $results"
            return xml ? results as XML : results as JSON
        } catch (Exception e) {
            e.printStackTrace()
            throw e
        }
    }

    List fireRules(String ruleSet, def facts) {
        RulesetDelegate ruleSetDelegate = ruleSetService.getRuleSet(ruleSet)
        if(!ruleSetDelegate) {
            throw new RulesetNotFoundException("Ruleset '$ruleSet' not found")
        }
        RulesEngine.process(ruleSetDelegate, facts)
        return facts
    }

    List parseXmlFacts(String facts) {
        def rootNode = XML.parse(facts)
        return getXMLFacts(rootNode)
    }

    List getXMLFacts(xml) {
        List theFacts = []
        xml.map.each {map ->
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
