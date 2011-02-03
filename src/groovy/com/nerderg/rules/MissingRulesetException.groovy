package com.nerderg.rules

/**
 * User: pmcneil
 * Date: 28/01/11
 *
 */
class MissingRulesetException extends Exception {

    MissingRulesetException() {
    }

    MissingRulesetException(String message) {
        super(message)
    }

    MissingRulesetException(String message, Throwable cause) {
        super(message, cause)
    }

    MissingRulesetException(Throwable cause) {
        super(cause)
    }
}
