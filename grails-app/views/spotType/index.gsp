
<%@ page import="org.nanocan.layout.SpotType" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'spotType.label', default: 'SpotType')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-spotType" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
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

    <div id="list-spotType" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="color" title="${message(code: 'spotType.color.label', default: 'Color')}" />
					
						<g:sortableColumn property="type" title="${message(code: 'spotType.type.label', default: 'Type')}" />
					
						<g:sortableColumn property="name" title="${message(code: 'spotType.name.label', default: 'Name')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${spotTypeInstanceList}" status="i" var="spotTypeInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${spotTypeInstance.id}">${fieldValue(bean: spotTypeInstance, field: "name")}</g:link></td>
					
						<td>${fieldValue(bean: spotTypeInstance, field: "type")}</td>
					
						<td><div id="colorPickDiv" style="float:left; background-color: ${spotTypeInstance?.color}; border: 1px solid; width:25px; height:25px;"/></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${spotTypeInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
