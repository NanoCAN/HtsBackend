<div id="show-wellLayout" class="content scaffold-show" role="main">
            <h1>Well ${wellLayoutInstance}</h1>
<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>
<ol class="property-list" style="margin: 0; padding: 0;">

    <g:if test="${wellLayoutInstance?.numberOfCellsSeeded}">
        <li class="fieldcontain">
            <span id="numberOfCellsSeeded-label" class="property-label"><g:message code="wellLayout.numberOfCellsSeeded.label" default="Number Of Cells Seeded" /></span>

            <span class="property-value" aria-labelledby="numberOfCellsSeeded-label"><g:link controller="numberOfCellsSeeded" action="show" id="${wellLayoutInstance?.numberOfCellsSeeded?.id}">${wellLayoutInstance?.numberOfCellsSeeded?.encodeAsHTML()}</g:link></span>

        </li>
    </g:if>

    <g:if test="${wellLayoutInstance?.cellLine}">
        <li class="fieldcontain">
            <span id="cellLine-label" class="property-label"><g:message code="wellLayout.cellLine.label" default="Cell Line" /></span>

            <span class="property-value" aria-labelledby="cellLine-label"><g:link controller="cellLine" action="show" id="${wellLayoutInstance?.cellLine?.id}">${wellLayoutInstance?.cellLine?.encodeAsHTML()}</g:link></span>

        </li>
    </g:if>

    <g:if test="${wellLayoutInstance?.inducer}">
        <li class="fieldcontain">
            <span id="inducer-label" class="property-label"><g:message code="wellLayout.inducer.label" default="Inducer" /></span>

            <span class="property-value" aria-labelledby="inducer-label"><g:link controller="inducer" action="show" id="${wellLayoutInstance?.inducer?.id}">${wellLayoutInstance?.inducer?.encodeAsHTML()}</g:link></span>

        </li>
    </g:if>

    <g:if test="${wellLayoutInstance?.sample}">
        <li class="fieldcontain">
            <span id="sample-label" class="property-label"><g:message code="wellLayout.sample.label" default="Sample" /></span>

            <span class="property-value" aria-labelledby="sample-label"><g:link controller="sample" action="show" id="${wellLayoutInstance?.sample?.id}">${wellLayoutInstance?.sample?.encodeAsHTML()}</g:link></span>

        </li>
    </g:if>

    <g:if test="${wellLayoutInstance?.spotType}">
        <li class="fieldcontain">
            <span id="spotType-label" class="property-label"><g:message code="wellLayout.spotType.label" default="Spot Type" /></span>

            <span class="property-value" aria-labelledby="spotType-label"><g:link controller="spotType" action="show" id="${wellLayoutInstance?.spotType?.id}">${wellLayoutInstance?.spotType?.encodeAsHTML()}</g:link></span>

        </li>
    </g:if>

    <g:if test="${wellLayoutInstance?.treatment}">
        <li class="fieldcontain">
            <span id="treatment-label" class="property-label"><g:message code="wellLayout.treatment.label" default="Treatment" /></span>

            <span class="property-value" aria-labelledby="treatment-label"><g:link controller="treatment" action="show" id="${wellLayoutInstance?.treatment?.id}">${wellLayoutInstance?.treatment?.encodeAsHTML()}</g:link></span>

        </li>
    </g:if>

    <g:if test="${wellLayoutInstance?.plateLayout}">
        <li class="fieldcontain">
            <span id="layout-label" class="property-label"><g:message code="wellLayout.layout.label" default="Layout" /></span>

            <span class="property-value" aria-labelledby="layout-label"><g:link controller="wellLayout" action="show" id="${wellLayoutInstance?.plateLayout?.id}">${wellLayoutInstance?.plateLayout?.encodeAsHTML()}</g:link></span>

        </li>
    </g:if>

</ol>
</div>