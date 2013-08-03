package com.nerderg.rules

/**
 * User: pmcneil
 * Date: 19/09/11
 *
 */
class RulesetNotFoundException extends Exception {

    RulesetNotFoundException() {
    }

    RulesetNotFoundException(String s) {
        super(s)
    }

    RulesetNotFoundException(String s, Throwable throwable) {
        super(s, throwable)
    }

    RulesetNotFoundException(Throwable throwable) {
        super(throwable)
    }
}
