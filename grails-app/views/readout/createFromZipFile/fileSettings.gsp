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
    <h1>Batch import readout data from zip file</h1>
    <g:form>
        <g:render template="readoutDataForm" model="${[fileEnding: fileEnding]}"/>

        <fieldset class="buttons">
            <g:submitButton name="readHeader" value="Read header from first file"/>
        </fieldset>

    </g:form>
</div>
</div>
</body>
</html>