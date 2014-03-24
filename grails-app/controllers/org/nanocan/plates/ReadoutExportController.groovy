package org.nanocan.plates

import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import org.codehaus.jackson.map.ObjectMapper
import org.hibernate.criterion.CriteriaSpecification

class ReadoutExportController {

    def springSecurityService
    def securityTokenService

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
            def meta = ["id", "Plate", "PlateRow", "PlateCol", "PlateLayout"]
            render meta as JSON
        }
        else{
            render status: 403
        }
    }

    def isSecurityTokenValid = {
        if(Readout.findByUuid(params.id)) render true
        else render false
    }

    def getReadoutIdFromSecurityToken = {
        def readoutInstance = Readout.findByUuid(params.id)
        if(readoutInstance){
            render readoutInstance.id
        }
    }

    def exportAsJSON = {
        def readoutInstance = Readout.get(params.id)

        if(accessAllowed(params.securityToken, readoutInstance)){
            def criteria = WellReadout.createCriteria()
            def result = criteria.list {
                eq("readout.id", params.long("id"))
                createAlias('readout', 'ro', CriteriaSpecification.LEFT_JOIN)
                createAlias('ro.plate', 'pl', CriteriaSpecification.LEFT_JOIN)
                createAlias('pl.plateLayout', 'playout', CriteriaSpecification.LEFT_JOIN)
                projections {
                    property "id"
                    property "pl.id"
                    property "row"
                    property "col"
                    property "playout.id"
                }
                order('row', 'desc')
                order('col', 'asc')
            }

            ObjectMapper mapper = new ObjectMapper()
            def jsonResult = mapper.writeValueAsString(result)

            response.contentType = "text/json"
            render jsonResult
        }
        else{
            render status: 403
        }
    }

}
