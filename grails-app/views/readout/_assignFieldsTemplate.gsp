<h3>Match columns and database properties:</h3><br/><br/>

<table>
    <g:set var="colCounter" value="${0}"/>
    <g:each in="${header}" var="col">

        <g:if test="${!col.equals("")}">
            <tr>
                <td>
                    ${col}
                </td>
                <td>
                    <g:select name="column_${colCounter}" noSelection="['':'Do not use']" value="${matchingMap[col]}" from="${readoutProperties}"/>
                </td>
            </tr>
        </g:if>


        <g:set var="colCounter" value="${++colCounter}" />
    </g:each>

</table>