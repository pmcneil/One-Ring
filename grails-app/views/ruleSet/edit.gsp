<%@ page import="com.nerderg.rules.RuleSet" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'ruleSet.label', default: 'RuleSet')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a>
    </span>
    <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label"
                                                                           args="[entityName]"/></g:link></span>
    <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label"
                                                                               args="[entityName]"/></g:link></span>
</div>

<div class="body">
    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${ruleSetInstance}">
        <div class="errors">
            <g:renderErrors bean="${ruleSetInstance}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <g:hiddenField name="id" value="${ruleSetInstance?.id}"/>
        <g:hiddenField name="version" value="${ruleSetInstance?.version}"/>
        <div class="dialog">
            <table>
                <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="name"><g:message code="ruleSet.name.label" default="Name"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: ruleSetInstance, field: 'name', 'errors')}">
                        <g:textField name="name" value="${ruleSetInstance?.name}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="ruleSet"><g:message code="ruleSet.ruleSet.label" default="Rule Set"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: ruleSetInstance, field: 'ruleSet', 'errors')}">
                        <div id="editor">${ruleSetInstance?.ruleSet}</div>
                        <g:textArea name="ruleSet" value="${ruleSetInstance?.ruleSet}" rows="40" cols="80"/>
                    </td>
                </tr>

                </tbody>
            </table>
        </div>

        <div class="buttons">
            <span class="button"><g:actionSubmit id="save" class="save" action="update"
                                                 value="${message(code: 'default.button.update.label', default: 'Update')}"/></span>
            <span class="button"><g:actionSubmit id="delete" class="delete" action="delete"
                                                 value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                                                 onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
