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
