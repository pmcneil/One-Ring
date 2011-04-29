<!DOCTYPE HTML>
<html>
<head>
    <title><g:layoutTitle default="Grails"/></title>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'icon32.png')}" type="image/png"/>
    <g:layoutHead/>
    <g:javascript library="application"/>
</head>
<body>
<div id="spinner" class="spinner" style="display:none;">
    <img src="${resource(dir: 'images', file: 'spinner.gif')}" alt="${message(code: 'spinner.alt', default: 'Loading...')}"/>
</div>
<div id="head"><a href="${createLink(uri: '/')}"><img class="logo" src="${resource(dir: 'images', file: 'oneRingLogo.png')}" alt="One Ring" border="0"/></a></div>
<g:layoutBody/>
<div id="ft">
    <hr/>
    Copyright &copy; nerdErg 2011 : version <g:meta name="app.version"/> Apache 2.0 License
</div>

</body>
</html>