<%@ page import="org.nanocan.plates.AssayType" %>


<div class="fieldcontain ${hasErrors(bean: assayTypeInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="assayType.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${assayTypeInstance?.name}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: assayTypeInstance, field: 'type', 'error')} required">
	<label for="type">
		<g:message code="assayType.type.label" default="Type" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="type" from="${assayTypeInstance.constraints.type.inList}" required="" value="${assayTypeInstance?.type}" valueMessagePrefix="assayType.type"/>

</div>

<div class="fieldcontain ${hasErrors(bean: assayTypeInstance, field: 'wavelength', 'error')} required">
	<label for="wavelength">
		<g:message code="assayType.wavelength.label" default="Wavelength" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="wavelength" type="number" value="${assayTypeInstance.wavelength}" required=""/>

</div>

