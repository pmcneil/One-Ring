#One Ring - Scripting Rules Engine Service

One Ring isn't like other "Rules Engines", it's meant to be used as a web service for multiple applications
to gain access to scripted processing of arbitrary parameters.

It centralises processing of common rules (or business rules) for multiple applications that need
access to the same rules. Rules are defined using a simple language understandable by domain experts.

One Ring is aimed at continuous processing for multiple small applications, not batch processing of billions of
entities. It is very light weight and is deployed as a WAR inside a container like Tomcat. For processing purposes
there is no reason why you can't have multiple One Ring servers running from the same Database.

It has not been optimised for speed, it's not fast but it's not exactly slow.

#Features

* A friendly to quite a few humans DSL
* REST and SOAP interfaces
* JSON and XML fact encoding
* Inbuilt rule testing in the rule set
* Script rules in simplified or not so simplified Groovy
* Keepin' it simple

#Roadmap

This project has not been updated since 2012 and relies on grails 1.3.x. We are looking at an upgrade path that turns this into a simple REST microservice running on Micronaut (https://micronaut.io/).

#Changes

Changes 15th May 2012

Added a "callRuleset" function to rules that lets you call another Ruleset from a rule like so:

ruleset("caller"){
    require(['a', 'b'])
    rule("call div"){
        when {
            b != 0
        }
        then {
            callRuleset("divide")
        }
        otherwise {
            result = 'Divide by zero'
        }
    }
}

ruleset("divide"){
    require(['a', 'b'])
    rule("div"){
        evaluate {
        println "$a / $b"
            result = a / b
        }
    }
}

Changes 5th March 2012

We now scrub JSONObject.Null values from incoming facts and convert to a null because of all the problems JSONObject.Null
causes.

Changes 20th December 2011

Made JSONObject.NULL fail a groovy truth test so that tests like if(blah) work as expected
References to maps within maps will now work so you can test S1.names.firstName == "Peter"
Improved the test error reporting
Test closures can now contain plain old groovy code so you can do things like:

test(name: [firstName:'Peter', secondName: 'McNeil') {
  println fact.name.firstName.class //for debugging
  assert fact.name.firstName == 'peter'
}

Changes 20th October 2011

One Ring has undergone quite a change. I have ripped out the online editing of rules, instead ruleSet files are
added to a directory (default ~/.OneRing/rules) which are read and parsed on startup and cached in memory (hashmap).
when a rule is called via the REST or SOAP interface the rules are pulled from the hashmap and run. The rules
remain the same until update is triggered via the web interface link, at which point the rules directory is read
and parsed again replacing the hashmap used to get rules. Update is asynchronous so rule requests can still be
served with the old rulesets while an update is happening.

We also broke out the rules engine into a separate project https://github.com/pmcneil/OneRing-Engine which generates
a very small jar that can be used to run tests on the rules inside your IDE.

This all leads to the ability to use your IDE or any editor to edit the rules and to version control the changes to the
rules using any version control system you like. The model I'm employing is to use a DVCS like git or mercurial
with my IDE to edit and test the rules then push to a central repository like github/bitbucket or your own. There the
rules can be "built" and tested using a continuous integration server like Jenkins or Bamboo, they can also be pulled
down to a test One Ring server to use in your test environment before pushing to production. Once happy that the rules
both pass tests and do what you want, you can pull them down to the production server and then at a time that suits you
simply update the production server.

Depending on your use-case you should be able to happily update the production server while it's being used since prior
requests will continue to use the old rules until a new ruleSet is requested. Note there is *no* multiple ruleset
transaction idea here, so if you rely on multiple rulesets in a row to run as a set, beware.

One Ring is Copyright 2010, 2011 Peter McNeil. It was developed in association with Legal Aid Commission (ACT) as
part of their eGrants project. Legal Aid Commission (ACT) has graciously allowed me to open source this code under
the Apache 2.0 License.

One Ring is Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.