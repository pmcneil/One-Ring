<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'ruleSet.label', default: 'RuleSet')}"/>
    <title>One Ring - list rules</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
  <span class="menuButton"><a class="home" href="${createLink(controller: 'ruleSet', action: 'update')}">Update</a></span>
</div>
<div class="body">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
  <g:if test="${flash.error}">
      <div class="errors">${flash.error}</div>
  </g:if>
    <div class="list">
        <table>
            <thead>
            <tr>
                Name
            </tr>
            </thead>
            <tbody>
            <g:each in="${ruleSetList}" status="i" var="ruleSet">
                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}" >

                    <td>${ruleSet.encodeAsHTML()}</td>

                </tr>
            </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${ruleSetTotal}"/>
    </div>
</div>
</body>
</html>
