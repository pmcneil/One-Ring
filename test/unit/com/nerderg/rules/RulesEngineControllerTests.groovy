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

import grails.test.*
import grails.converters.JSON

class RulesEngineControllerTests extends ControllerUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testFireJson1() {
        mockLogging(RulesEngineService, true)
        mockLogging(RulesEngine, true)
        def ruleSet = """
        ruleset("Means Test") {
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
            test(income: 900, expenses: 501) {
                incomeTest 'passed'
                nett_income 399
            }
        }"""

        def rssControl = mockFor(RuleSetService)
        rssControl.demand.getRuleSet(1..1) { String name ->
            def rulesets = RulesEngine.processRules(ruleSet)
            return rulesets[0]
        }
        controller.rulesEngineService = new RulesEngineService()
        controller.rulesEngineService.ruleSetService = rssControl.createMock()
        controller.params.ruleSet = "Means Test"
        controller.params.facts = "[{id: 1, income: 900, expenses: 501},{id: 2, income: 2000, expenses: 600}]"
        controller.fire()
        println controller.response.contentAsString
        //this may be fragile if the order changes
        def expected = '[{"nett_income":399,"id":1,"incomeTest":"passed","income":900,"expenses":501},{"nett_income":1400,"id":2,"incomeTest":"failed","income":2000,"expenses":600}]'
        assert controller.response.contentAsString == expected
    }

    void testFireJson2() {
        mockLogging(RulesEngineService, true)
        mockLogging(RulesEngine, true)
        def ruleSet = """ruleset("Means Test") {
            require(['income', 'expenses'])
            rule("nett income") {
                when {
                    income == "heaps"
                }
                then {
                    incomeTest = 'passed'
                }
                otherwise {
                    incomeTest = 'failed'
                }
            }
        }"""
        def rssControl = mockFor(RuleSetService)
        rssControl.demand.getRuleSet(1..1) { String name ->
            def rulesets = RulesEngine.processRules(ruleSet)
            return rulesets[0]
        }
        controller.rulesEngineService = new RulesEngineService()
        controller.rulesEngineService.ruleSetService = rssControl.createMock()
        controller.params.ruleSet = "Means Test"
        controller.params.facts = """[{id: 1, income: lots, expenses: 501},{id: 2, income: 2000, expenses: 600}]"""
        controller.fire()
        println controller.response.contentAsString
        def expected = '[{"id":1,"incomeTest":"failed","income":"lots","expenses":501},{"id":2,"incomeTest":"failed","income":2000,"expenses":600}]'
        assert controller.response.contentAsString == expected
    }

    void testFireJson3() {
        mockLogging(RulesEngineService, true)
        mockLogging(RulesEngine, true)
        def ruleSet = """ruleset("Means Test") {
            require(['income', 'expenses'])
            rule("nett income") {
                when {
                    income == "23heaps"
                }
                then {
                    incomeTest = 'passed'
                }
                otherwise {
                    incomeTest = 'failed'
                }
            }
        }"""
        def rssControl = mockFor(RuleSetService)
        rssControl.demand.getRuleSet(1..1) { String name ->
            def rulesets = RulesEngine.processRules(ruleSet)
            return rulesets[0]
        }
        controller.rulesEngineService = new RulesEngineService()
        controller.rulesEngineService.ruleSetService = rssControl.createMock()
        controller.params.ruleSet = "Means Test"
        controller.params.facts = """[{id: 1, income: 23heaps, expenses: 501},{id: 2, income: 2000, expenses: 600}]"""
        controller.fire()
        println controller.response.contentAsString
        def expected = '[{"id":1,"incomeTest":"passed","income":"23heaps","expenses":501},{"id":2,"incomeTest":"failed","income":2000,"expenses":600}]'
        assert controller.response.contentAsString == expected
    }

    void testFireXml1() {
        mockLogging(RulesEngineService, true)
        mockLogging(RulesEngine, true)
        def ruleSet = """ruleset("Means Test") {
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
            test(income: 900, expenses: 501) {
                incomeTest 'passed'
                nett_income 399
            }
        }"""
        def rssControl = mockFor(RuleSetService)
        rssControl.demand.getRuleSet(1..1) { String name ->
            def rulesets = RulesEngine.processRules(ruleSet)
            return rulesets[0]
        }
        controller.rulesEngineService = new RulesEngineService()
        controller.rulesEngineService.ruleSetService = rssControl.createMock()
        controller.params.ruleSet = "Means Test"
        controller.params.facts = """<list><map><entry key="income">900</entry><entry key="expenses">300</entry></map></list>"""
        controller.fire()
        println controller.response.contentAsString
        def expected = '<?xml version="1.0" encoding="UTF-8"?><list><map><entry key="income">900</entry><entry key="expenses">300</entry><entry key="nett_income">600</entry><entry key="incomeTest">failed</entry></map></list>'
        assert controller.response.contentAsString == expected
    }

    void testFireXml2() {
        mockLogging(RulesEngineService, true)
        mockLogging(RulesEngine, true)
        def ruleSet = """ruleset("Means Test") {
            require(['income', 'expenses'])
            rule("nett income") {
                when {
                    income == "heaps"
                }
                then {
                    incomeTest = 'passed'
                }
                otherwise {
                    incomeTest = 'failed'
                }
            }
        }"""
        def rssControl = mockFor(RuleSetService)
        rssControl.demand.getRuleSet(1..1) { String name ->
            def rulesets = RulesEngine.processRules(ruleSet)
            return rulesets[0]
        }
        controller.rulesEngineService = new RulesEngineService()
        controller.rulesEngineService.ruleSetService = rssControl.createMock()
        controller.params.ruleSet = "Means Test"
        controller.params.facts = """<list><map><entry key="income">lots</entry><entry key="expenses">300</entry></map></list>"""
        controller.fire()
        println controller.response.contentAsString
        def expected = '<?xml version="1.0" encoding="UTF-8"?><list><map><entry key="income">lots</entry><entry key="expenses">300</entry><entry key="incomeTest">failed</entry></map></list>'
        assert controller.response.contentAsString == expected
    }

    void testFireXml3() {
        mockLogging(RulesEngineService, true)
        mockLogging(RulesEngine, true)
        def ruleSet = """ruleset("Means Test") {
            require(['income', 'expenses'])
            rule("nett income") {
                when {
                    income == "23heaps"
                }
                then {
                    incomeTest = 'passed'
                }
                otherwise {
                    incomeTest = 'failed'
                }
            }
        }"""
        def rssControl = mockFor(RuleSetService)
        rssControl.demand.getRuleSet(1..1) { String name ->
            def rulesets = RulesEngine.processRules(ruleSet)
            return rulesets[0]
        }
        controller.rulesEngineService = new RulesEngineService()
        controller.rulesEngineService.ruleSetService = rssControl.createMock()
        controller.params.ruleSet = "Means Test"
        controller.params.facts = """<list><map><entry key="income">23heaps</entry><entry key="expenses">300</entry></map></list>"""
        controller.fire()
        println controller.response.contentAsString
        def expected = '<?xml version="1.0" encoding="UTF-8"?><list><map><entry key="income">23heaps</entry><entry key="expenses">300</entry><entry key="incomeTest">passed</entry></map></list>'
        assert controller.response.contentAsString == expected
    }

    void testJSONEncoder() {
        def map = [[
                M8: [order: 4,
                        fap: [
                                Does_Fred_Smith_have_a_Health_Care_Card_or_Pensioner_Concession_Card: [
                                        yes: 'on',
                                        Card_Number: 2345,
                                        Expiry_date: '23/01/2014',
                                        Attach_a_copy_of_the_card: 'M8.Attach_a_copy_of_the_card-DSCF0014.JPG'
                                ],
                                Does_Jane_Smith_have_a_Health_Care_Card_or_Pensioner_Concession_Card: [
                                        Card_Number: null,
                                        Expiry_date: null,
                                        Attach_a_copy_of_the_card: 'none'
                                ]
                        ]
                ],
                M7: [
                        order: 3,
                        Do_you_have_a_Health_Care_Card_or_Pensioner_Concession_Card: [
                                Card_Number: null, Expiry_date: null, Attach_a_copy_of_the_card: 'none'
                        ]
                ],
                M6: [order: 2,
                        fap: [
                                Does_Jane_Smith_get_a_pension_or_benefit_from_Centrelink_or_the_Department_of_Veterans_Affairs: [
                                        Weekly_income_before_tax: null,
                                        Which_payments: [Other: [give_details: null]]
                                ],
                                Does_Fred_Smith_get_a_pension_or_benefit_from_Centrelink_or_the_Department_of_Veterans_Affairs: [
                                        yes: 'on',
                                        Weekly_income_before_tax: 32.99,
                                        Which_payments: [
                                                Mature_Age_Allowance_or_pension_benefit: 'on',
                                                Other: [give_details: null]
                                        ]
                                ]
                        ]
                ],
                applyNext: 'Continue',
                M5: [order: 1,
                        Do_you_get_a_pension_or_benefit_from_Centrelink_or_the_Department_of_Veterans_Affairs: [
                                yes: 'on',
                                Centrelink_Reference_Number_CRN_or_DVA_reference_number: 666666,
                                Weekly_income_before_tax: 11.76,
                                Which_payments: [
                                        Newstart_Allowance: 'on',
                                        Other: [give_details: null]]
                        ]
                ],
                next: ['M9', 'M10', 'M11', 'M12'],
                applicationId: 3,
                S1: [
                        message: 'Client details do not match the file.',
                        order: 3,
                        file: [
                                dateOfBirth: '01/01/2000',
                                lastName: 'Smith-User',
                                title: 'Mr',
                                givenNames: 'Garry & John',
                                fileNumber: '08C304456X',
                                firmNumber: 30043,
                                fileStatus: 'active',
                                gender: 'Male',
                                clientNumber: 190556,
                                dateReceived: '01/01/1990'
                        ],
                        Have_you_represented_this_client_before_in_a_Legal_Aid_assisted_case: [
                                yes: 'on',
                                Please_enter_a_recent_previous_Legal_Aid_ACT_file_number: '08C304456X'
                        ],
                        pass: false
                ],
                fap: ['Fred Smith', 'Jane Smith'],
                M4: [order: 0, Are_you_currently_employed_own_a_small_business_or_a_farmer: [yes: 'on', What_type_of_work_do_you_do: 'book blower']],
                M3: [order: 2, faps: [yes: 'on', Full_name: ['Fred Smith', 'Jane Smith'], Relationship_to_you: ['kin', 'kin']]],
                M2: [order: 1, Do_you_have_any_dependant_children: [yes: 'on', How_many: 2]], loginType: 'PP',
                G9: [order: 2, restriction: [In_prison_or_detained: [Other: [Give_detils: null]]]],
                M1: [order: 0, Do_you_have_a_spouse_or_partner_living_with_you: 'on'],
                G4: [order: 1, Date_of_birth: '01/01/2000'],
                G3: [order: 0, Have_you_applied_for_legal_aid_before: [In_which_year: null]],
                G6: [Country_of_birth: 'Australia', order: 1],
                G5: [order: 2, Gender: 'Male'],
                firmNumber: 30043,
                G2: [order: 0, names: [Given_Names: 'Peter', Last_or_Family_Name: 'McNeil', aliases: [Type_of_name: null, Other_name: null], Title: 'Mr']],
                action: 'applyNext',
                controller: 'grant'
        ]]
        println map as JSON
    }

    void testNullCollectionHandling() {

        def facts = """[{id: 1, income: 23heaps, expenses: 501},{id: 2, income: 2000, expenses: 600}, null]"""
        def list = controller.cleanUpJSONNullCollection(JSON.parse(facts))
        assert list.get(2) == null

    }

}
