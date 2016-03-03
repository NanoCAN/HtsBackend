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
import org.nanocan.layout.SlideLayout
import org.nanocan.project.Experiment
import org.nanocan.project.Project

@Secured(['ROLE_USER'])
class PlateController {

    def importReadoutData(){

    }

    def index() {
        //deal with max
        if(!params.max && session.maxPlate) params.max = session.maxPlate
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        session.maxPlate = params.max

        //deal with offset
        params.offset = params.offset?:(session.offsetPlateResult?:0)
        session.offsetPlateResult = params.offset

        def plateInstanceList
        def plateInstanceListTotal

        if(session.experimentSelected)
        {
            plateInstanceList = []
            Experiment.get(session.experimentSelected).plateLayouts.each{
                plateInstanceList.addAll(Plate.findAllByPlateLayout(it))
            }
            plateInstanceList.unique()
        }
        else if(session.projectSelected)
        {
            plateInstanceList = []
            def experiments = Experiment.findAllByProject(Project.get(session.projectSelected as Long))
            experiments.each{
                it.plateLayouts.each{ pl ->
                    plateInstanceList.addAll(Plate.findAllByPlateLayout(pl))
                }
            }
            plateInstanceList.unique()
        }
        else
        {
            plateInstanceList = Plate.list(params)
            plateInstanceListTotal = Plate.count()
        }

        if (session.experimentSelected || session.projectSelected)
        {
            plateInstanceListTotal = plateInstanceList?.size()?:0
            if (params.int('offset') > plateInstanceListTotal) params.offset = 0

            if(plateInstanceListTotal > 0)
            {
                int rangeMin = Math.min(plateInstanceListTotal, params.int('offset'))
                int rangeMax = Math.min(plateInstanceListTotal, (params.int('offset') + params.int('max')))

                plateInstanceList = plateInstanceList.asList()
                if(params.sort) plateInstanceList = plateInstanceList.sort{it[params.sort]}
                else plateInstanceList.sort{ a,b -> a.id <=> b.id}
                if(params.order == "desc") plateInstanceList = plateInstanceList.reverse()

                plateInstanceList = plateInstanceList.subList(rangeMin, rangeMax)
            }
        }

        [plateInstanceList: plateInstanceList, plateInstanceTotal: plateInstanceListTotal]
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
