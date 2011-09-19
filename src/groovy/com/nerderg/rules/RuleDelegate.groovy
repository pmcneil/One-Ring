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

/**
 * User: pmcneil
 * Date: 28/01/11
 *
 */
class RuleDelegate {

    String name
    Map fact
    Boolean result = false

    def when(Closure cl) {
        result = RulesEngine.with(fact, cl)
//        println "when $name result is $result"
    }

    def then(Closure cl) {
        if (result) {
//            println "then $name"
            RulesEngine.with(fact, cl)
        }
    }

    def otherwise(Closure cl) {
        if (!result) {
//            println "otherwise $name"
            RulesEngine.with(fact, cl)
        }
    }

    def evaluate(Closure cl) {
//        println "eval $name"
        result = RulesEngine.with(fact, cl)
    }
}
