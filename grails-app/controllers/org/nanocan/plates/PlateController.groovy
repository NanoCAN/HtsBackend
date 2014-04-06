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

@Secured(['ROLE_USER'])
class PlateController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def importReadoutData(){

    }
    /*
    def getDataOrigins(){

        def labwares = Labware.findAllByBarcode(params.id)
        def plateInstance = Plate.findById(params.id)

        render (template: "data_origins", model:[plateInstance: plateInstance, labwares: labwares, barcode: params.id])
    }

    def getRawValues(){
        println params

        def rawData = RawData.findAllByBarcodeAndRun("E1S1M1D1", 522).sort{ a, b -> a.wellNum <=> b.wellNum}

        render(template: "heatmap", model: [rawData: rawData, plateInstance: Plate.get(params.id)])
    }
      */

    def scaffold = true
}
