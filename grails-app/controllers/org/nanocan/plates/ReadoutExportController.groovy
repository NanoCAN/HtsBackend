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

import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import org.codehaus.jackson.map.ObjectMapper
import org.hibernate.criterion.CriteriaSpecification
import org.nanocan.layout.PlateLayout
import org.nanocan.layout.WellLayout

class ReadoutExportController {

    def springSecurityService
    def securityTokenService
    def readoutExportService

    private def accessAllowed = { securityToken, readoutInstance ->
        //check if user is authenticated
        if(!springSecurityService.isLoggedIn()){
            //alternatively check if a security token is provided
            if(!securityToken || securityToken != securityTokenService.getSecurityToken(readoutInstance)){
                return(false)
            }

        }
        return(true)
    }

    def exportMetaDataAsJSON = {
        def readoutInstance = Readout.get(params.id)

        if(accessAllowed(params.securityToken, readoutInstance)){
            def meta = ["id","Plate", "DateOfReadout", "PlateRow", "PlateCol", "Replicate", "PlateLayout", "PlateReadout", "AssayName", "AssayType"]
            render meta as JSON
        }
        else{
            render status: 403
        }
    }

    def exportPlateLayoutMetaDataAsJSON = {
        def plateLayoutInstance = PlateLayout.get(params.id)

        if(accessAllowed(params.securityToken, plateLayoutInstance)){
            def meta = ["id","PlateLayout", "PlateRow", "PlateCol", "AmountOfCells", "CellLine", "Inducer", "InducerConcentration", "Treatment", "Sample", "Control", "Accession", "AccessionType"]
            render meta as JSON
        }
        else{
            render status: 403
        }
    }


    def isSecurityTokenValid = {
        if(Readout.findByUuid(params.id)) render true
        else if(Plate.findByUuid(params.id)) render true
        else render false
    }

    def getReadoutIdFromSecurityToken = {
        def readoutInstance = Readout.findByUuid(params.id)
        if(readoutInstance){
            render readoutInstance.id
        }
        else render error: 404
    }

    def getPlateLayoutIdFromSecurityToken = {
        def plateLayoutInstance = PlateLayout.findByUuid(params.id)
        if(plateLayoutInstance){
            render plateLayoutInstance.id
        }
        else render error: 404
    }

    def getReadoutSecurityTokensFromPlateSecurityToken = {
        def plateInstance = Plate.findByUuid(params.id)
        if(accessAllowed(params.id, plateInstance)){
            def tokens = plateInstance.readouts.collect{securityTokenService.getSecurityToken(it)}
            render tokens as JSON
        }
        else render status: 403
    }

    def getPlateLayoutSecurityTokenFromPlateSecurityToken = {
        def plateInstance = Plate.findByUuid(params.id)
        if(accessAllowed(params.id, plateInstance)){
            def token = [securityTokenService.getSecurityToken(plateInstance.plateLayout)]
            render token as JSON
        }
        else render status: 403
    }

    @Secured(['ROLE_USER'])
    def createUrlForR = {

        //if we don't remove this, it'll override the action setting below
        params.remove("_action_createUrlForR")

        def baseUrl = g.createLink(controller: "readoutExport", absolute: true).toString()
        baseUrl = baseUrl.substring(0, baseUrl.size()-5)
        def securityToken = securityTokenService.getSecurityToken(Readout.get(params.id))

        [baseUrl: baseUrl, slideInstanceId: params.id, securityToken: securityToken]
    }

    def exportAsJSON = {
        def readoutInstance = Readout.get(params.id)

        if(accessAllowed(params.securityToken, readoutInstance)){
            def result = readoutExportService.getWellReadouts(params.long("id"))

            ObjectMapper mapper = new ObjectMapper()
            def jsonResult = mapper.writeValueAsString(result)

            response.contentType = "text/json"
            render jsonResult
        }
        else{
            render status: 403
        }
    }

    def exportPlateLayoutAsJSON = {
        def plateLayoutInstance = PlateLayout.get(params.id)

        if(accessAllowed(params.securityToken, plateLayoutInstance)){
            def result = readoutExportService.getPlateLayout(params.long("id"))

            ObjectMapper mapper = new ObjectMapper()
            def jsonResult = mapper.writeValueAsString(result)

            response.contentType = "text/json"
            render jsonResult
        }
        else{
            render status: 403
        }
    }

    @Secured(['ROLE_USER'])
    def download = {
        def result = readoutExportService.getWellReadoutsWithLayout(params.long("id"))

        def fileEnding = "csv"
        if(params.separator == "\t") fileEnding = "txt"

        response.setHeader("Content-disposition", "filename=${slideInstance.toString().replace(" ", "_")}.${fileEnding}")
        response.contentType = "application/vnd.ms-excel"

        def outs = response.outputStream

        def header = meta.join(params.separator)

        results.each() {

            outs << it.join(params.separator)
            outs << "\n"
        }
        outs.flush()
        outs.close()
        return
    }

}
