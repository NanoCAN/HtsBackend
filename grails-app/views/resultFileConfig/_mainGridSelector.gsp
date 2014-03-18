<div class="fieldcontain ${hasErrors(bean: resultFileConfigInstance, field: 'mainColCol', 'error')} ">
    <label for="mainColCol">
        <g:message code="resultFileConfig.blockCol.label" default="MainCol Col"/>

    </label>
    <g:textField name="mainColCol" value="${resultFileConfigInstance?.mainColCol}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: resultFileConfigInstance, field: 'mainRowCol', 'error')} ">
    <label for="mainRowCol">
        <g:message code="resultFileConfig.blockCol.label" default="MainRow Col"/>

    </label>
    <g:textField name="mainRowCol" value="${resultFileConfigInstance?.mainRowCol}"/>
</div>