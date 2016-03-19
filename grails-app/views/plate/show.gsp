
<%@ page import="org.nanocan.plates.Plate" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'plate.label', default: 'Plate')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>

        <r:script>$(function() {
            $("#accordion").accordion({
                collapsible:true,
                autoHeight: false
            });

        });</r:script>

    </head>
	<body>
		<a href="#show-plate" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="navbar">
        <div class="navbar-inner">
            <div class="container">
                <ul class="nav">
                    <g:render template="/templates/navmenu"></g:render>
                    <li><g:link class="list" action="index"><g:message code="default.list.label"
                                                                      args="[entityName]"/></g:link></li>
                    <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                                          args="[entityName]"/></g:link></li>
                </ul>
            </div>
        </div>
    </div>

		<div id="show-plate" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>

            <div id="accordion" style="margin: 25px; width: 90%;">
                <h3><a href="#">Properties</a></h3>
                <div>
			    <ol class="property-list plate">
			
				<g:if test="${plateInstance?.barcode}">
				<li class="fieldcontain">
					<span id="barcode-label" class="property-label"><g:message code="plate.barcode.label" default="Barcode" /></span>
					
						<span class="property-value" aria-labelledby="barcode-label"><g:fieldValue bean="${plateInstance}" field="barcode"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${plateInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="plate.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${plateInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${plateInstance?.plateType}">
				<li class="fieldcontain">
					<span id="plateType-label" class="property-label"><g:message code="plate.plateType.label" default="Plate Type" /></span>
					
						<span class="property-value" aria-labelledby="plateType-label"><g:link controller="plateType" action="show" id="${plateInstance?.plateType?.id}">${plateInstance?.plateType?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${plateInstance?.format}">
				<li class="fieldcontain">
					<span id="format-label" class="property-label"><g:message code="plate.format.label" default="Format" /></span>
					
						<span class="property-value" aria-labelledby="format-label"><g:fieldValue bean="${plateInstance}" field="format"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${plateInstance?.replicate}">
				<li class="fieldcontain">
					<span id="replicate-label" class="property-label"><g:message code="plate.replicate.label" default="Replicate" /></span>
					
						<span class="property-value" aria-labelledby="replicate-label"><g:fieldValue bean="${plateInstance}" field="replicate"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${plateInstance?.experiment}">
				<li class="fieldcontain">
					<span id="experiment-label" class="property-label"><g:message code="plate.experiment.label" default="Experiment" /></span>
					
						<span class="property-value" aria-labelledby="experiment-label"><g:link controller="experiment" action="show" id="${plateInstance?.experiment?.id}">${plateInstance?.experiment?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${plateInstance?.plateLayout}">
				<li class="fieldcontain">
					<span id="plateLayout-label" class="property-label"><g:message code="plate.plateLayout.label" default="Plate Layout" /></span>
					
						<span class="property-value" aria-labelledby="plateLayout-label"><g:link controller="plateLayout" action="show" id="${plateInstance?.plateLayout?.id}">${plateInstance?.plateLayout?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>

                </ol>
                </div>
                <h3><a href="#">Readouts</a></h3>
                <div>
                    <g:if test="${plateInstance?.readouts}">

                    <g:each in="${plateInstance.readouts}" var="r">
                    <span class="property-value" aria-labelledby="readouts-label"><g:link controller="readout" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></span></br>
                    </g:each>

                    </g:if>
                    <g:else>
                        No readout data found...
                    </g:else>
                    <li class="add">
                        <g:link controller="readout" action="create" params="['plate.id': plateInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'readout.label', default: 'Readout')])}</g:link>
                    </li>
                </div>
             </div>

			<g:form controller="plate" method="DELETE">
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${plateInstance?.id}" />
					<g:link class="edit" action="edit" id="${plateInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>

