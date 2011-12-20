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

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.web.json.JSONObject

class BootStrap {

    def ruleSetService

    def init = { servletContext ->
        JSONObject.NULL.metaClass.asBoolean = { return false }
        File rulesDir = new File(ConfigurationHolder.config.oneRing.rules.directory as String)
        if (!rulesDir.exists()) {
            rulesDir.mkdirs()
            File meansTest = new File(rulesDir, "Means Test.ruleset")
            meansTest.setText("""ruleset("Means Test") {
    require(['income', 'expenses'])
    rule("nett income") {
        when {
            nett_income = income - expenses
            nett_income < 400.00
        }
        then {
            incomeTest = 'passed'
        }
        otherwise {
            incomeTest = 'failed'
        }
    }

    rule("nett income2") {
        evaluate {
            incomeTest2 = nett_income < 400.00
        }
    }

    rule("nett income3") {
        if(fact.nett_income > 400) {
            fact.incomeTest3 = 'rich bugger'
        } else {
            fact.incomeTest3 = 'poor bugger'
        }
    }

    test(income: 900, expenses: 501) {
        incomeTest 'passed'
        nett_income 399
    }
}""")

            File questions = new File(rulesDir, "Questions.ruleset")
            questions.setText("""ruleset("Questions") {

    require(['code'])

    def questions = ['428606': 'do you want fries with that?', '428605': 'supersize that coke for ya?']

    rule("Question Prospects") {
        fact.question = questions[fact.code]
    }

    test(code: '428606') {question 'do you want fries with that?'}
    test(code: '428605') {question 'supersize that coke for ya?'}
    test(code: '428607') {question null}
}
""")
        }

        ruleSetService.update()
    }

    def destroy = {
    }
}
