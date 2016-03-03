<!-- Progress bar -->
<g:jprogressDialog message="Adding readout data to database..." progressId="${progressId}" trigger="buttonAddSpots" />

<div style="padding:50px;">
    <g:render template="assignFieldsTemplate" model="${[header: header]}"/>

    <fieldset class="buttons">
        <g:submitToRemote onLoading="\$('#formDiv').hide() ; \$('#updateDiv').hide();" onSuccess="\$('#updateDiv').show();"
                              update="updateDiv" action="processResultFile" name="buttonAddSpots" value="Add readout data"/>
    </fieldset>
</div>