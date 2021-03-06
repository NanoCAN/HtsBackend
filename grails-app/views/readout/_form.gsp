<%@ page import="org.nanocan.plates.AssayType; org.nanocan.plates.Plate; org.nanocan.plates.Readout" %>

<head>
<r:script>
            $(document).ready(function(){

               $('#resultFileAjax').autocomplete({
                    source: '<g:createLink controller='resultFile' action='ajaxResultFileFinder'/>',
                    minLength: 0,
                    select: function(event, ui)
                    {
                        document.getElementById('resultFile.id').setAttribute('value', ui.item.id);
                    }
                });

                $('#resultImageAjax').autocomplete({
                    source: '<g:createLink controller='resultFile' action='ajaxResultImageFinder'/>',
                    minLength: 0,
                    select: function(event, ui)
                    {
                        document.getElementById('resultImage.id').setAttribute('value', ui.item.id);
                    }
                });

               $('#protocolAjax').autocomplete({
                    source: '<g:createLink controller='resultFile' action='ajaxProtocolFinder'/>',
                    minLength: 0,
                    select: function(event, ui)
                    {
                        document.getElementById('protocol.id').setAttribute('value', ui.item.id);
                    }
                });
            });
</r:script>
</head>

<div class="fieldcontain ${hasErrors(bean: readoutInstance, field: 'dateOfReadout', 'error')} ">
    <label for="dateOfReadout">
        <g:message code="experiment.dateOfReadout.label" default="Day Of Readout" />

    </label>
    <g:jqDatePicker name="dateOfReadout" value="${readoutInstance?.dateOfReadout}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: readoutInstance, field: 'assay', 'error')} ">
	<label for="assay">
		<g:message code="readout.assayType.label" default="Assay Type" />
		
	</label>
	<g:select id="assay" name="assay.id" from="${AssayType.list()}" optionKey="id" optionValue="name" required="" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: readoutInstance, field: 'plate', 'error')} required">
	<label for="plate">
		<g:message code="readout.plate.label" default="Plate" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="plate" name="plate.id" from="${Plate.list()}" optionKey="id" required="" value="${readoutInstance?.plate?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: readoutInstance, field: 'resultFile', 'error')} required">
    <label for="resultFile">
        <g:message code="readout.resultFile.label" default="Result File" />
        <span class="required-indicator">*</span>
    </label>
    <div style="float:right; padding-right: 120px;"><table><tr><td>Choose existing file: </td>
        <td> <input type="text" id="resultFileAjax"></td></tr>
        <input type="hidden" id="resultFile.id" name="resultFile.id" value="${readoutInstance?.resultFile?.id}">
        <tr><td>...or upload new file: </td><td><input type="file" id="resultFile.input" name="resultFileInput"/></td></tr></table></div>
</div><br/><br/><br/><br/>

<div class="fieldcontain ${hasErrors(bean: readoutInstance, field: 'resultImage', 'error')} ">
    <label for="resultImage">
        <g:message code="slide.resultImage.label" default="Result Image" />
    </label>
    <div style="float:right; padding-right: 120px;"><table><tr><td>Choose existing file: </td>
        <td> <input type="text" id="resultImageAjax"></td></tr>
        <input type="hidden" id="resultImage.id" name="resultImage.id" value="${readoutInstance?.resultImage?.id}">
        <tr><td>...or upload new file: </td><td><input type="file" id="resultImage.input" name="resultImageInput"/></td></tr></table></div>
</div> <br/><br/><br/><br/>

<div class="fieldcontain ${hasErrors(bean: readoutInstance, field: 'protocol', 'error')} ">
    <label for="protocol">
        <g:message code="slide.protocol.label" default="Experiment Protocol" />
    </label>
    <div style="float:right; padding-right: 120px;"><table><tr><td>Choose existing file: </td>
        <td> <input type="text" id="protocolAjax"></td></tr>
        <input type="hidden" id="protocol.id" name="protocol.id" value="${readoutInstance?.protocol?.id}">
        <tr><td>...or upload new file: </td><td><input type="file" id="protocol.input" name="protocolInput"/></td></tr></table></div>
</div>
