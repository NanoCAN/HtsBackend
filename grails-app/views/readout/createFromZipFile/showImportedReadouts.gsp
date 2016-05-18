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
    <h1>Batch import</h1>

    <div class="message">Batch import completed successfully.</div>

    <ul style="padding-left:40px;">
    <g:each in="${listOfReadouts}" var="readout">
        <li><g:link controller="plate" action="show" id="${readout?.plate?.id}">${readout.plate}</g:link></li>
    </g:each>
    </ul>
</div>
</body>
</html>