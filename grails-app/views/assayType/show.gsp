
<%@ page import="org.nanocan.plates.AssayType" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'assayType.label', default: 'AssayType')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-assayType" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
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
		<div id="show-assayType" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list assayType">
			
				<g:if test="${assayTypeInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="assayType.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${assayTypeInstance}" field="name"/></span>
					
				</li>
				</g:if>

				<g:if test="${assayTypeInstance?.type}">
					<li class="fieldcontain">
						<span id="type-label" class="property-label"><g:message code="assayType.type.label" default="Type" /></span>

						<span class="property-value" aria-labelledby="type-label"><g:fieldValue bean="${assayTypeInstance}" field="type"/></span>

					</li>
				</g:if>
			
				<g:if test="${assayTypeInstance?.wavelength}">
				<li class="fieldcontain">
					<span id="wavelength-label" class="property-label"><g:message code="assayType.wavelength.label" default="Wavelength" /></span>
					
						<span class="property-value" aria-labelledby="wavelength-label"><g:fieldValue bean="${assayTypeInstance}" field="wavelength"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:assayTypeInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${assayTypeInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
