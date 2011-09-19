package com.nerderg.rules

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import groovy.io.FileType

class RuleSetService {

    static transactional = false
    private HashMap<String, RulesetDelegate> ruleSets = [:]
    private HashMap<String, Date> ruleSetTimeStamps = [:]
    private def ruleLock

    def update() {
        //just replace the referenced map
        ruleSets = (HashMap)readRules()
    }

    private Map<String, RulesetDelegate> readRules() {
        long start = System.currentTimeMillis()
        HashMap<String, RulesetDelegate> ruleSets = [:]
        StringBuffer ruleSetStrings = new StringBuffer()
        def nameMatch = ~/.*\.ruleset/
        File dir = new File(ConfigurationHolder.config.oneRing.rules.directory as String)
        if (dir.exists() && dir.isDirectory()) {
            long startProcess = System.currentTimeMillis()
            dir.eachFileRecurse(FileType.FILES) { File ruleFile ->
                if (ruleFile.name ==~ nameMatch) {
                    ruleSetStrings.append("\n")
                    ruleSetStrings.append(ruleFile.getText())
                }
            }
            log.debug "read files in " + (System.currentTimeMillis() - startProcess) + "ms"
            startProcess = System.currentTimeMillis()
            List<RulesetDelegate> ruleSetDelegates = RulesEngine.processRules(ruleSetStrings.toString())
            log.debug "processed rule sets in " + (System.currentTimeMillis() - startProcess) + "ms"
            ruleSetDelegates.each { ruleSet ->
                List<String> fails = RulesEngine.testRuleset(ruleSet)
                if(fails.size() > 0) {
                    throw new Exception("Ruleset $ruleSet.name failed tests\n" + fails.join(',\n'))
                }
                ruleSets.put(ruleSet.name, ruleSet)
            }
        }
        log.debug "Read rules in " + (System.currentTimeMillis() - start) + "ms"
        return Collections.unmodifiableMap(ruleSets)
    }

    RulesetDelegate getRuleSet(String name) {
        return ruleSets[name]
    }

    Integer size() {
        return ruleSets.size()
    }

    Set<String> getRuleSetNames() {
        ruleSets.keySet()
    }
}
