/*
 * Copyright (C) 2014
 * Center for Excellence in Nanomedicine (NanoCAN)
 * Molecular Oncology
 * University of Southern Denmark
 * ###############################################
 * Written by:	Markus List
 * Contact: 	mlist'at'health'.'sdu'.'dk
 * Web:			http://www.nanocan.org/miracle/
 * ###########################################################################
 *	
 *	This file is part of MIRACLE.
 *
 *  MIRACLE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with this program. It can be found at the root of the project page.
 *	If not, see <http://www.gnu.org/licenses/>.
 *
 * ############################################################################
 */
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
        result.each{
            it[2] = getRowChar(it[2])
        }

        result = [ data: result, meta: [
                "id": [type: "num"],
                "Value": [type: "num"],
                "Row": [type: "cat"],
                "Column": [type: "num"]
        ]]

        ObjectMapper mapper = new ObjectMapper()
        def jsonResult = mapper.writeValueAsString(result)

        response.contentType = "text/json"
        render jsonResult
    }

    private getRowChar(int row) {
        return row > 0 && row < 27 ? String.valueOf((char)(row + 64)) : null;
    }
}

