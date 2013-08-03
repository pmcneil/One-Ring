package com.nerderg.rules

import groovy.io.FileType
import javax.naming.Context
import javax.naming.InitialContext

class RuleSetService {

    def grailsApplication
    static transactional = false
    private HashMap<String, RulesetDelegate> ruleSets = [:]
    private HashMap<String, Date> ruleSetTimeStamps = [:]
    private String rulesDirectory

    def update() {
        //just replace the referenced map
        ruleSets = (HashMap) readRules()
    }

    private String getRulesDir() {
        if (rulesDirectory) {
            return rulesDirectory
        }
        try {
            Context initContext = new InitialContext()
            Context envContext = initContext.lookup("java:comp/env")
            rulesDirectory = envContext.lookup("rulesDirectory")
        } catch (e) {
            println "rulesDirectory from environment context not set $e"
        }
        if (rulesDirectory) {
            println "Overriding rulesDirectory from context env (e.g. tomcat context.xml): $rulesDirectory"
        } else {
            rulesDirectory = grailsApplication.config.oneRing.rules.directory
        }
        return rulesDirectory
    }

    private Map<String, RulesetDelegate> readRules() {
        long start = System.currentTimeMillis()
        HashMap<String, RulesetDelegate> ruleSets = [:]
        StringBuffer ruleSetStrings = new StringBuffer()
        def nameMatch = ~/.*\.ruleset/
        File dir = new File(getRulesDir())
        log.info "Rules directory is ${dir.absolutePath}"
        if (dir.exists() && dir.isDirectory()) {
            long startProcess = System.currentTimeMillis()
            dir.eachFileRecurse(FileType.FILES) { File ruleFile ->
                if (ruleFile.name ==~ nameMatch) {
                    ruleSetStrings.append("\n")
                    ruleSetStrings.append(ruleFile.getText())
                }
            }
            log.debug "read files in " + (System.currentTimeMillis() - startProcess) + "ms"
            File tmpOut = File.createTempFile("rules", ".groovy")
            tmpOut.write(ruleSetStrings.toString())
            startProcess = System.currentTimeMillis()
            List<RulesetDelegate> ruleSetDelegates = RulesEngine.processRules(ruleSetStrings.toString())
            log.debug "processed rule sets in " + (System.currentTimeMillis() - startProcess) + "ms"
            ruleSetDelegates.each { ruleSet ->
                List<String> fails = RulesEngine.testRuleset(ruleSet)
                if (fails.size() > 0) {
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
