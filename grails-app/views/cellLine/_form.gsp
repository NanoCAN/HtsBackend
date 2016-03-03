<%@ page import="org.nanocan.layout.CellLine" %>

<head>
    <r:require module="colorPicker"/>
</head>

<r:script>
                $('#colorPickDiv').ColorPicker({
                    color: '${cellLineInstance?.color}',
                    onShow: function (colpkr) {
                        $(colpkr).fadeIn(500);
                        return false;
                    },
                    onHide: function (colpkr) {
                        $(colpkr).fadeOut(500);
                        return false;
                    },
                    onChange: function (hsb, hex, rgb) {
                        $('#colorPickDiv').css('backgroundColor', '#' + hex);
                        $('#colorInput').attr("value", "#" + hex);
                    }
                });

           $("#olfAutoComplete").autocomplete({
            source: function(request, response){
                $.ajax({
                    url: "<g:createLink controller='cellLine' action='searchOpenLabFrameworkCellLineData'/>",
                    data: request,
                    success: function(data){
                        response(data);
                    },
                    error: function(){
                        alert("Unable to retrieve data from OpenLabFramework");
                    }
                });
            },
            minLength: 2,
            select: function(event, ui) {
                $("#openLabFrameworkId").val(ui.item.id);
                $("#openLabFrameworkTitle").val(ui.item.label);
            }
           });
</r:script>

<div class="fieldcontain ${hasErrors(bean: cellLineInstance, field: 'name', 'error')} ">
    <label for="name">
        <g:message code="cellLine.name.label" default="Name"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="name" value="${cellLineInstance?.name}"/>
</div>

<div style="width:75px; padding-left:210px;" class="fieldcontain ${hasErrors(bean: cellLineInstance, field: 'color', 'error')} ">
    <label for="color">
        <g:message code="cellLine.color.label" default="Color"/>
        <span class="required-indicator">*</span>
    </label>
    <div id="colorPickDiv" style="float:right; background-color: ${cellLineInstance?.color}; border: 1px solid; width:25px; height:25px;">
        <input type="hidden" id="colorInput" name="color" value="${cellLineInstance?.color}">
    </div>
</div>

<div class="fieldcontain ${hasErrors(bean: cellLineInstance, field: 'openLabFrameworkId', 'error')} ">
    <label for="openLabFrameworkId">
        <g:message code="cellLine.openLabFrameworkId.label" default="OpenLabFramework Cell Line"/>

    </label>
    <g:hiddenField name="openLabFrameworkId"/>
    <g:hiddenField name="openLabFrameworkTitle"/>
    <g:textField name="olfAutoComplete" style="width:300px;"/>
</div>

