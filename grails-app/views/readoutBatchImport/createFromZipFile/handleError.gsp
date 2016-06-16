<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <meta name="layout" content="body">
    <g:set var="entityName" value="${message(code: 'sheet.label', default: 'Sheet')}" />
    <title><g:message code="default.edit.label" args="[entityName]" /></title>
</head>
<body>

<div class="content">
    <h1>Batch import failed</h1>

    <div class="errors">The import failed due to the following reason:</br>${error}</div>
</div>
</body>
</html>