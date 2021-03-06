
<%@ page import="org.nanocan.project.Project" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'project.label', default: 'Project')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-project" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
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
		<div id="list-project" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
                        <g:sortableColumn property="projectTitle" title="${message(code: 'project.projectTitle.label', default: 'Project Title')}" />

                        <th><g:message code="project.createdBy.label" default="Created By" /></th>

						<g:sortableColumn property="dateCreated" title="${message(code: 'project.dateCreated.label', default: 'Date Created')}" />

                        <th><g:message code="project.lastUpdatedBy.label" default="Last Updated By" /></th>

						<g:sortableColumn property="lastUpdated" title="${message(code: 'project.lastUpdated.label', default: 'Last Updated')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${projectInstanceList}" status="i" var="projectInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${projectInstance.id}">${fieldValue(bean: projectInstance, field: "projectTitle")}</g:link></td>

                        <td>${fieldValue(bean: projectInstance, field: "createdBy")}</td>

						<td><g:formatDate type="date" date="${projectInstance.dateCreated}" /></td>

                        <td>${fieldValue(bean: projectInstance, field: "lastUpdatedBy")}</td>

						<td><g:formatDate type="date" date="${projectInstance.lastUpdated}" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${projectInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
