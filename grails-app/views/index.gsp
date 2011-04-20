<html>
<head>
    <title>One Ring</title>
    <meta name="layout" content="main"/>

</head>
<body>
<div class="nav">
    <span class="menuButton"><a href="${g.createLink(controller: 'ruleSet', action: 'index')}">Edit Rules</a></span>
    <span class="menuButton"><a href="${g.resource(dir: 'rest/applyRules')}">REST</a></span>
    <span class="menuButton"><a href="${g.resource(dir: 'services')}">SOAP</a></span>
</div>
<div id="pageBody" class="body">
    <h1>One Ring - Scripting Rules Engine Service</h1>
    <p>
        One Ring isn't like other &quot;Rules Engines&quot;, it's meant to be used as a web service for multiple applications
        to gain access to scripted processing of arbitrary parameters.
    </p>
    <p>
        It centralises storage and processing of common rules (or business rules) for multiple applications that need
        access to the same rules. Rules are defined using a simple language understandable by domain experts.
    </p>
    <p>
        One Ring is aimed at continuous processing for multiple small applications, not batch processing of billions of
        entities. It is very light weight and is deployed as a WAR inside a container like Tomcat. For processing purposes
        there is no reason why you can't have multiple One Ring servers running from the same Database.
    </p>
    <p>
        It has not been optimised for speed, but it's not exactly slow.
    </p>
    <h1>Features</h1>
    <ul>
        <li>A friendly to quite a few humans DSL</li>
        <li>REST and SOAP interfaces</li>
        <li>JSON and XML fact encoding</li>
        <li>Inbuilt rule testing in the rule set</li>
        <li>Script rules in simplified or not so simplified Groovy</li>
        <li>Keepin' it simple</li>
    </ul>
    <h1>REST</h1>
    <p>
        Get started using the RESTful interface by pointing your app (a browser will do) at <a href="${g.resource(dir: 'rest/applyRules')}">${g.resource(dir: 'rest/applyRules')}</a>
        and add a couple of parameters like:
    </p>
    <ul>
        <li>ruleSet=Means Test</li>
        <li>income=900</li>
        <li>expenses=400</li>
    </ul>
    <br/>
    <p>->
        <a href="${createLink(uri: '/rest/applyRules')}?ruleSet=Means Test&facts=[{income:900,expenses:400}]">
            ${createLink(uri: '/rest/applyRules')}?ruleSet=Means Test&facts=[{income:900,expenses:400}]
        </a>
    </p>
    <p>
        You can GET or POST your facts to the rules engine as JSON, and it will return a JSON map/object of the results.
    </p>
    <p>
        If you're a masochist you can post the facts as (encoded) XML and get XML results. e.g.
    <div class="code">
        &lt;list&gt;
        &lt;map&gt;
        &lt;entry key="income"&gt;900&lt;/entry&gt;
        &lt;entry key="expenses"&gt;400&lt;/entry&gt;
        &lt;/map&gt;
        &lt;/list&gt;
    </div>
</p>
    <p>->
        <a href="/rulesEngine/rest/applyRules?ruleSet=Means%20Test&facts=%20%3Clist%3E%3Cmap%3E%3Centry%20key=%22income%22%3E900%3C/entry%3E%3Centry%20key=%22expenses%22%3E300%3C/entry%3E%3C/map%3E%3C/list%3E">
            /rulesEngine/rest/applyRules?ruleSet=Means%20Test&facts=%20%3Clist%3E%3Cmap%3E%3Centry%20key=%22income%22%3E900%3C/entry%3E%3Centry%20key=%22expenses%22%3E400%3C/entry%3E%3C/map%3E%3C/list%3E
        </a>
    </p>

    <h1>SOAP</h1>
    <p>
        Get started with the SOAP interface by pointing your browser at <a href="${g.resource(dir: 'services')}">${g.resource(dir: 'services')}</a> then take in the WSDL.
    </p>
    <p>
        You can use JSON or XML encoded facts, JSON:
    <div class="code">
        &lt;soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:rul="http://rules.nerderg.com/"&gt;
        &lt;soapenv:Header/&gt;
        &lt;soapenv:Body&gt;
        &lt;rul:applyRules&gt;
        &lt;rul:ruleSet&gt;Means Test&lt;/rul:ruleSet&gt;
        &lt;rul:facts&gt;[{income: 900, expenses: 600}, {income:900, expenses: 300}]&lt;/rul:facts&gt;
        &lt;/rul:applyRules&gt;
        &lt;/soapenv:Body&gt;
        &lt;/soapenv:Envelope&gt;
    </div>
    Note the XML escapery going on here for the XML encoded facts:
    <div class="code">
        &lt;soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:rul="http://rules.nerderg.com/"&gt;
        &lt;soapenv:Header/&gt;
        &lt;soapenv:Body&gt;
        &lt;rul:applyRules&gt;
        &lt;rul:ruleSet&gt;Means Test&lt;/rul:ruleSet&gt;
        &lt;rul:facts&gt;&lt;![CDATA[
        &lt;list&gt;
        &lt;map&gt;
        &lt;entry key="income"&gt;900&lt;/entry&gt;
        &lt;entry key="expenses"&gt;300&lt;/entry&gt;
        &lt;/map&gt;
        &lt;/list&gt;
        ]]&gt;&lt;/rul:facts&gt;
        &lt;/rul:applyRules&gt;
        &lt;/soapenv:Body&gt;
        &lt;/soapenv:Envelope&gt;
    </div>
</p>

</div>
</body>
</html>
