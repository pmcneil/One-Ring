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

import com.nerderg.rules.RuleSet

class BootStrap {

    def init = { servletContext ->
        def rs = RuleSet.findByName("Means Test")
        if (!rs) {
            new RuleSet(name: "Means Test", ruleSet: """ruleset("Means Test") {
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
}""").save()
            new RuleSet(name: "Questions",
                    ruleSet: """ruleset("Questions") {

    require(['code'])

    def questions = ['428606': 'do you want fries with that?', '428605': 'supersize that coke for ya?']

    rule("Question Prospects") {
        fact.question = questions[fact.code]
    }

    test(code: '428606') {question 'do you want fries with that?'}
    test(code: '428605') {question 'supersize that coke for ya?'}
    test(code: '428607') {question null}
}
""").save()
        }
    }
    def destroy = {
    }
}
