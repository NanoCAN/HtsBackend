
<%@ page import="org.nanocan.layout.NumberOfCellsSeeded" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'numberOfCellsSeeded.label', default: 'NumberOfCellsSeeded')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-numberOfCellsSeeded" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="navbar">
            <div class="navbar-inner">
                <div class="container">
                    <ul class="nav">
                        <g:render template="/templates/navmenu"></g:render>
                        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                    </ul>
                </div>
            </div>
        </div>
		<div id="show-numberOfCellsSeeded" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list numberOfCellsSeeded">
			
				<g:if test="${numberOfCellsSeededInstance?.color}">
				<li class="fieldcontain">
					<span id="color-label" class="property-label"><g:message code="numberOfCellsSeeded.color.label" default="Color" /></span>

                    <span style="width: 25px;" class="property-value" aria-labelledby="color-label">
                        <div id="colorPickDiv" style="float:right; background-color: ${numberOfCellsSeededInstance?.color}; border: 1px solid; width:25px; height:25px;"/>
                    </span>
				</li>
				</g:if>
			
				<g:if test="${numberOfCellsSeededInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="numberOfCellsSeeded.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${numberOfCellsSeededInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form method="DELETE">
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${numberOfCellsSeededInstance?.id}" />
					<g:link class="edit" action="edit" id="${numberOfCellsSeededInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
