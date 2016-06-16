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
            <li><g:link class="list" action="show" id="${readoutInstance.id}">Back to Readout</g:link></li>
                </ul>
            </div>
        </div>
    </div>

    <div class="content">
    <h1>Import Readout Data</h1>
        <g:form>

            <g:hiddenField name="id" value="${readoutInstance.id}"></g:hiddenField>

            <g:render template="/readoutBatchImport/readoutDataForm" model="${[fileEnding: fileEnding]}"/>

            <fieldset class="buttons">
                <g:submitToRemote update="headerSelection" action="readInputFile" name="buttonReadInputFile" value="Read selected sheet"/>
            </fieldset>

            <div id="headerSelection"/>
        </g:form>
        </div>
    </div>
    </body>
</html>