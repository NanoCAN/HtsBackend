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
            def meta = ["id","Plate", "PlateRow", "PlateCol", "PlateLayout", "PlateReadout"]
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

    def getReadoutSecurityTokensFromPlateSecurityToken = {
        def plateInstance = Plate.findByUuid(params.id)
        if(accessAllowed(params.id, plateInstance)) render plateInstance.readouts.collect{it.uuid} as JSON
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

    def getTypeOfReadout = {
        def readoutInstance = Readout.get(params.id)

        if(accessAllowed(params.securityToken, readoutInstance))
            render readoutInstance.typeOfReadout
        else render error: 403
    }

    def getDateOfReadout = {
        def readoutInstance = Readout.get(params.id)

        if(accessAllowed(params.securityToken, readoutInstance))
            render readoutInstance.dateOfReadout
        else render error: 403
    }

    def getAssayType = {
        def readoutInstance = Readout.get(params.id)

        if(accessAllowed(params.securityToken, readoutInstance))
            render readoutInstance.assayType
        else render error: 403
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
                    property "measuredValue"
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
