ruleset("Prospects") {

    require(['workItemCode'])

    def questions = ['428606': 'do you want fries with that?', '428605': 'supersize that coke for ya?']

    rule("Question Prospects") {
        println "questions[${fact.workItemCode}] is  ${questions[fact.workItemCode]}"
        println "workItemCode type is " + fact.workItemCode.class
        fact.question = questions[fact.workItemCode]
    }

    test(workItemCode: '428606') {question 'do you want fries with that?'}
    test(workItemCode: '428605') {question 'supersize that coke for ya?'}
}
