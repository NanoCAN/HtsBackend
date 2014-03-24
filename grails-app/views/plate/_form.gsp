<%@ page import="org.nanocan.plates.Plate" %>

<div class="fieldcontain">
    <label for="controlPlate">
        <g:message code="plate.controlPlate.label" default="Control Plate?" />
    </label>
    <g:checkBox name="controlPlate"/>
</div>

<div class="fieldcontain ${hasErrors(bean: plateInstance, field: 'barcode', 'error')} ">
	<label for="barcode">
		<g:message code="plate.barcode.label" default="Barcode" />
		
	</label>
	<g:textField name="barcode" value="${plateInstance?.barcode}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: plateInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="plate.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${plateInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: plateInstance, field: 'plateType', 'error')} ">
	<label for="plateType">
		<g:message code="plate.plateType.label" default="Plate Type" />
		
	</label>
	<g:select id="plateType" name="plateType.id" from="${org.nanocan.plates.PlateType.list()}" optionKey="id" value="${plateInstance?.plateType?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: plateInstance, field: 'format', 'error')} required">
	<label for="format">
		<g:message code="plate.format.label" default="Format" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="format" from="${plateInstance.constraints.format.inList}" required="" value="${plateInstance?.format}" valueMessagePrefix="plate.format"/>
</div>

<div class="fieldcontain ${hasErrors(bean: plateInstance, field: 'replicate', 'error')} required">
	<label for="replicate">
		<g:message code="plate.replicate.label" default="Replicate" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="replicate" type="number" value="${plateInstance.replicate}" required=""/>
</div>


<div class="fieldcontain ${hasErrors(bean: plateInstance, field: 'experiment', 'error')} required">
	<label for="experiment">
		<g:message code="plate.experiment.label" default="Experiment" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="experiment" name="experiment.id" from="${org.nanocan.project.Experiment.list()}" optionKey="id" required="" value="${plateInstance?.experiment?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: plateInstance, field: 'plateLayout', 'error')} required">
	<label for="plateLayout">
		<g:message code="plate.plateLayout.label" default="Plate Layout" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="plateLayout" name="plateLayout.id" from="${org.nanocan.layout.PlateLayout.list()}" optionKey="id" required="" value="${plateInstance?.plateLayout?.id}" class="many-to-one"/>
</div>

