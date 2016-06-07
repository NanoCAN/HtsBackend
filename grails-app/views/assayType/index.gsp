
<%@ page import="org.nanocan.plates.AssayType" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'assayType.label', default: 'AssayType')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-assayType" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
	<div class="navbar">
		<div class="navbar-inner">
			<div class="container">
				<ul class="nav">
					<g:render template="/templates/navmenu"></g:render>
					<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
				</ul>
			</div>
		</div>
	</div>
		<div id="list-assayType" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="name" title="${message(code: 'assayType.name.label', default: 'Name')}" />

						<g:sortableColumn property="type" title="${message(code: 'assayType.type.label', default: 'Type')}" />
					
						<g:sortableColumn property="wavelength" title="${message(code: 'assayType.wavelength.label', default: 'Wavelength')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${assayTypeInstanceList}" status="i" var="assayTypeInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${assayTypeInstance.id}">${fieldValue(bean: assayTypeInstance, field: "type")}</g:link></td>
					
						<td>${fieldValue(bean: assayTypeInstance, field: "name")}</td>
					
						<td>${fieldValue(bean: assayTypeInstance, field: "wavelength")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${assayTypeInstanceTotal ?: 0}" />
			</div>
		</div>
	</body>
</html>
