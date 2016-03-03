<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'sheet.label', default: 'Sheet')}" />
    <title><g:message code="default.edit.label" args="[entityName]" /></title>
</head>
<body>

<div class="navbar">
    <div class="navbar-inner">
        <div class="container">
            <ul class="nav">
                <g:render template="/templates/navmenu"></g:render>
            </ul>
        </div>
    </div>
</div>

<div class="content">
    <g:form>

    <fieldset class="form">
        <g:render template="assignFieldsTemplate" model="${[header: header]}"/>
    </fieldset>

    <fieldset class="buttons">
        <g:submitButton name="process" value="Import readout files"/>
    </fieldset>

    </g:form>
</div>
</div>
</body>
</html>