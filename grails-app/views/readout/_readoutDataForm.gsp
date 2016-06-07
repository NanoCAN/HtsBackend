<div id="updateDiv" class="message">Select a sheet and configuration</div>

<div id="formDiv">

    <ol class="property-list">

        %{--                <li class="fieldcontain">
                            <span class="property-label">Select a suitable mapping of column names to properties (optional):</span>
                            <span class="property-value"><g:select name="config" noSelection="['':'']" value="${readoutInstance?.lastConfig?.id}" from="${configs}" optionKey="id"></g:select></span>
                        </li>--}%

        <li class="fieldcontain">
            <span class="property-label">Skip lines:</span>

            <span class="property-value">
                <g:checkBox name="skipLines" value="${false}"/>
                <g:field name="howManyLines" value="${0}" type="number"/>
            </span>
        </li>

        <li class="fieldcontain">
            <span class="property-label">Minimum number of columns to read:</span>

            <span class="property-value">
                <g:field name="minColRead" value="${1}" type="number"/>
            </span>
        </li>

        <g:if test="${fileEnding == "xlsx" || fileEnding == "xls"}">
            <li class="fieldcontain">
                <span class="property-label">Select the sheet with the results of the readout: </span>
                <span class="property-value"><g:select name="sheet" from="${sheets}" optionKey="index" optionValue="name"></g:select>
                </span>
            </li>
        </g:if>

        <g:elseif test="${fileEnding == "txt"}">
            <li class="fieldcontain">
                <span class="property-label">Select between CSV (comma-separated) and CSV2 (semicolon-separated): </span>
                <span class="property-value"><g:select name="csvType" from="${['CSV', 'CSV2', 'custom']}"
                                                       onchange="${g.remoteFunction(update: 'custom_CSV_div', controller: 'readout', action: 'customCSV', params: '\'selectedType=\'+this.value')}"></g:select>
                </span>
            </li>
            <div id="custom_CSV_div"/>
        </g:elseif>
    </ol>