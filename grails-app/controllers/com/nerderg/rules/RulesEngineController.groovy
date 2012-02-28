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

import grails.converters.JSON
import grails.converters.XML
import org.codehaus.groovy.grails.web.json.JSONObject
import sun.security.provider.certpath.CollectionCertStore

class RulesEngineController {

    def rulesEngineService
    static allowedMethods = [fire: ["POST", "GET"]]

    def fire = {
        if (request.format == 'json') {
            forward(action: 'firejson')
            return
        }
        if (request.format == 'xml') {
            forward(action: 'firexml')
            return
        }
        //ok text/html form perhaps. Now we need to work out if the facts are encoded XML or JSON
        log.debug "fire params $params"
        def ruleSet = params.ruleSet
        if (!ruleSet) {
            log.error "No rule set supplied"
            response.status = 400
            return render("Error: No rule set supplied")
        }
        def facts = params.facts
        if (!facts) {
            log.error "No facts supplied"
            response.status = 400
            return render("Error: No facts supplied")
        }
        try {
            boolean xml = facts?.trim()?.startsWith("<")
            def theFacts = xml ? rulesEngineService.parseXmlFacts(facts) : JSON.parse(facts)
            if (xml) {
                render fireRules(ruleSet, theFacts) as XML
            } else {
                render fireRules(ruleSet, theFacts) as JSON
            }
        } catch (e) {
            log.error e
            response.status = 500
            render "Error: '$e'"
        }
    }

    def firejson = {
        def json = request.JSON
        log.debug "fire JSON params $request.JSON"
        def ruleSet = json.ruleSet
        def facts = cleanUpJSONNullCollection(json.facts)
        def res = fireRules(ruleSet, facts)
        println "about to render ${res as JSON}"
        render res as JSON
    }

    def firexml = {
        log.debug "fire XML params $request.XML"
        def xml = request.XML
        def ruleSet = xml.ruleSet.text()
        def facts = rulesEngineService.getXMLFacts(xml.facts.list)
        render fireRules(ruleSet, facts) as XML
    }

    private def fireRules(ruleSet, facts) {
        if (ruleSet && facts) {
            try {
                def results = rulesEngineService.fireRules(ruleSet, facts)
                log.debug "render json $results"
                return results
            } catch (e) {
                int lineNumber = e.stackTrace.find {
                    it.fileName == 'Script1.groovy'
                }?.lineNumber
                log.error "Error processing rule $ruleSet -> $e line $lineNumber"
                response.status = 500
                return [[error: e.message]]
            }
        } else {
            response.status = 400
            def msg = "Error in request: '" + (ruleSet ? "" : " ruleSet is not set. ") +
                    (facts ? "" : " facts not set.") + "'"
            return [[error: msg]]
        }

    }

    private Map cleanUpJSONNullMap(Map m) {
        m.each {
            if (it.value.equals(null)) {
                it.value = null
            } else if (it.value instanceof Map) {
                it.value = cleanUpJSONNullMap(it.value)
            } else if (it.value instanceof Collection) {
                it.value = cleanUpJSONNullCollection(it.value)
            }
        }
    }

    private Collection cleanUpJSONNullCollection(Collection c) {
        //create a new collection sans JSONObject.Null objects
        List collect = []
        c.each { v ->
            if (!v.equals(null)) {
                if (v instanceof Collection) {
                    collect.add(cleanUpJSONNullCollection(v))
                }
                if (v instanceof Map) {
                    collect.add(cleanUpJSONNullMap(v))
                }
                collect.add(v)
            }
        }
        return collect
    }
}
