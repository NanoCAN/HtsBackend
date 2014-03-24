package org.nanocan.plates

import grails.plugins.springsecurity.Secured
import org.codehaus.jackson.map.ObjectMapper

class ReadoutHeatmapController {

    @Secured(['ROLE_USER'])
    def exportWellReadoutForHeatmapAsJSON = {

        def criteria = WellReadout.createCriteria()
        def result = criteria.list {
            eq("readout.id", params.long("id"))
            projections {
                property("id", "id")
                property("measuredValue", "Value")
                property("row", "Row")
                property("col", "Column")
            }
            order('row', 'asc')
            order('col', 'asc')
        }

/*        //log2 transform and round signal
        result.each{
            if(it[1]==0) it[1]=null
            else if (it[1]==null) return
            else{
                it[1] = Math.round(Math.log(it[1])/Math.log(2))
            }
        }*/

        result = [ data: result, meta: [
                "id": [type: "num"],
                "Value": [type: "num"],
                "Row": [type: "num"],
                "Column": [type: "num"]
        ]]

        ObjectMapper mapper = new ObjectMapper()
        def jsonResult = mapper.writeValueAsString(result)

        response.contentType = "text/json"
        render jsonResult
    }
}

