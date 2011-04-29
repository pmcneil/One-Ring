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

class RulesEngineController {

    def rulesEngineService
    static allowedMethods = [fire: ["POST", "GET"]]

    def fire = {
        def ruleSet = params.ruleSet
        def facts = params.facts
        if (ruleSet && facts) {
            try {
                render rulesEngineService.applyRules(ruleSet, facts)
            } catch (e) {
                log.error e
                e.printStackTrace()
                response.status = 500
                render "Error: '$e'"
            }
        } else {
            response.status = 400
            render "Error in request: '" + (ruleSet ? "" : " ruleSet is not set. ") +
                    (facts ? "" : " facts not set.") + "'"
        }
    }
}
