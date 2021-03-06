<%@ page import="org.nanocan.layout.Sample" %>



<div class="fieldcontain ${hasErrors(bean: sampleInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="sample.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${sampleInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: sampleInstance, field: 'controlType', 'error')} ">
	<label for="controlType">
		<g:message code="sample.controlType.label" default="Control Type" />
		
	</label>
	<g:select name="controlType" from="${sampleInstance.constraints.controlType.inList}" value="${sampleInstance?.controlType}" valueMessagePrefix="sample.controlType" noSelection="['': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: sampleInstance, field: 'type', 'error')} ">
	<label for="type">
		<g:message code="sample.type.label" default="Type" />
		
	</label>
	<g:select name="type" from="${sampleInstance.constraints.type.inList}" value="${sampleInstance?.type}" valueMessagePrefix="sample.type" noSelection="['': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: sampleInstance, field: 'target', 'error')} ">
	<label for="target">
		<g:message code="sample.target.label" default="Target" />
		
	</label>
	<g:textField name="target" value="${sampleInstance?.target}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: sampleInstance, field: 'color', 'error')} ">
	<label for="color">
		<g:message code="sample.color.label" default="Color" />
		
	</label>
	<g:textField name="color" value="${sampleInstance?.color}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: sampleInstance, field: 'control', 'error')} ">
	<label for="control">
		<g:message code="sample.control.label" default="Control" />
		
	</label>
	<g:checkBox name="control" value="${sampleInstance?.control}" />
</div>

<div class="fieldcontain ${hasErrors(bean: sampleInstance, field: 'identifiers', 'error')} ">
	<label for="identifiers">
		<g:message code="sample.identifiers.label" default="Identifiers" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${sampleInstance?.identifiers?}" var="i">
    <li><g:link controller="identifier" action="show" id="${i.id}">${i?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="identifier" action="create" params="['sample.id': sampleInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'identifier.label', default: 'Identifier')])}</g:link>
</li>
</ul>

</div>

