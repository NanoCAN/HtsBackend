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
import org.nanocan.layout.WellLayout

class ReadoutScatterController {

    @Secured(['ROLE_USER'])
    def exportWellReadoutForScatterPlotAsJSON = {

        def readout = Readout.get(params.id)

        def layoutWells = readout.plate.plateLayout.wells
        def wells = readout.wells

        def points = layoutWells.collect{ layout ->
            def point = [:]

            point.Sample = layout?.sample?.name?:""
            point.Control = layout?.sample?.control?"Control":"Sample"
            def wellReadout = wells.find{ well -> well.row == layout.row && well.col == layout.col}
            point.Value = wellReadout.measuredValue?:0
            point.id = wellReadout.id

            return(point)
        }

        def result = [ data: points, meta: [
                "id": [type: "num"],
                "Value": [type: "num"],
                "Sample": [type: "cat"],
                "Control": [type: "cat"]
        ]]

        ObjectMapper mapper = new ObjectMapper()
        def jsonResult = mapper.writeValueAsString(result)

        response.contentType = "text/json"
        render jsonResult
    }
}

