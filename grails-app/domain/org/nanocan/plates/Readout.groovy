package org.nanocan.plates

import org.nanocan.file.PlateResultFileConfig
import org.nanocan.file.ResultFile

class Readout {

    static belongsTo = [plate: Plate]

    String typeOfReadout
    int wavelength
    String assayType
    static hasMany = [wells: WellReadout]
    ResultFile resultFile
    ResultFile resultImage
    ResultFile protocol
    PlateResultFileConfig lastConfig
    Date dateOfReadout
    String uuid

    static constraints = {
        uuid nullable: true
        lastConfig nullable: true
        resultImage nullable: true
        protocol nullable: true
        typeOfReadout inList: ['Fluorescence']
        assayType inList: ['Cell Titer Blue']
    }

    String toString(){
        "${dateOfReadout.toString()} - ${typeOfReadout} - ${assayType}"
    }
}
