package com.nerderg.rules

class RuleSet {

    String name
    String ruleSet

    static constraints = {
        name(unique: true)
    }
}
