<%@ page import="org.nanocan.project.Project; org.nanocan.project.Experiment" %>



<div class="fieldcontain ${hasErrors(bean: experimentInstance, field: 'title', 'error')} ">
	<label for="title">
		<g:message code="experiment.title.label" default="Title" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="title" value="${experimentInstance?.title}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: experimentInstance, field: 'firstDayOfTheExperiment', 'error')} ">
	<label for="firstDayOfTheExperiment">
		<g:message code="experiment.firstDayOfTheExperiment.label" default="First Day Of The Experiment" />
		<span class="required-indicator">*</span>
	</label>
	<g:jqDatePicker name="firstDayOfTheExperiment" value="${experimentInstance?.firstDayOfTheExperiment}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: experimentInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="experiment.description.label" default="Description" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="description" value="${experimentInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: experimentInstance, field: 'project', 'error')} required">
	<label for="project">
		<g:message code="experiment.project.label" default="Project" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="project" name="project.id" from="${Project.list()}" optionKey="id" required="" value="${experimentInstance?.project?.id}" class="many-to-one"/>
</div>



