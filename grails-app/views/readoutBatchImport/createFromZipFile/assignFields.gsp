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

<div class="content" id="messageDiv">


    <div class="message">Assign fields in the readout files to numeric or alphanumeric well position (or row/column if these two are in separate columns) and readout signal (measuredValue).</div>

    <g:jprogressDialog message="Importing readout data..." progressId="${uuid}" trigger="_eventId_process"/>

    <g:formRemote name="assignFieldsForm" url="[controller: 'readoutBatchImport', action: 'createFromZipFile']" update="messageDiv">

    <input type="hidden" id="execution" name="execution" value="${request.flowExecutionKey}"/>
    <input type="hidden" id="_eventId" name="_eventId" value="process"/>

    <fieldset class="form">
        <g:render template="/readout/assignFieldsTemplate" model="${[header: header]}"/>
    </fieldset>

    <fieldset class="buttons">
        <g:submitButton name="process" value="Import readout files"/>
    </fieldset>

    </g:formRemote>
</div>
</div>
</body>
</html>