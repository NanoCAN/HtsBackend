<!doctype html>
<html xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="main">
    <title>Create readout from zipped file</title>
</head>
<body>

<div class="navbar">
    <div class="navbar-inner">
        <div class="container">
            <ul class="nav">
                <g:render template="/templates/navmenu"></g:render>
            </ul>
        </div>
    </div>
</div>

<div style="padding-left:10px; ">
    <h1>Batch import readout data from zip file</h1>
    <hr/>
    <br/>
    <g:if test="${flash.error}">
        <div class="errors" role="alert">&nbsp;${flash.error}</div>
        ${flash.error = ''}
    </g:if>
    <g:if test="${flash.okay}">
        <div class="message" role="status">${flash.okay}</div>
        ${flash.okay = ''}
    </g:if>

    <g:uploadForm method="POST">
        <fieldset class="form">
            <div class="fieldcontain ${hasErrors(bean: readoutInstance, field: 'dateOfReadout', 'error')} ">
                <label for="dateOfReadout">
                    <g:message code="experiment.dateOfReadout.label" default="Day Of Readout" />

                </label>
                <g:jqDatePicker name="dateOfReadout" value="${readoutInstance?.dateOfReadout}"/>
            </div>

            <div class="fieldcontain ${hasErrors(bean: readoutInstance, field: 'assay', 'error')} ">
                <label for="assay">
                    <g:message code="readout.assayType.label" default="Assay" />

                </label>
                <g:select id="assay" name="assay.id" from="${org.nanocan.plates.AssayType.list()}" optionKey="id" optionValue="name" required="" class="one-to-one"/>
            </div>

        <div class="fieldcontain">

            <span class="property-label">ZIP archive with readout files:</span>

            <span class="property-value">
                <input type="file" name="zippedFile" style="width:400px;" />
            </span>
        </div>
        </fieldset>
        <fieldset class="buttons">
            <g:submitButton class="save" name="upload" value="Upload zip file"/>
        </fieldset>
    </g:uploadForm>
</div>
</body>
</html>