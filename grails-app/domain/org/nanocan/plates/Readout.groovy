package org.nanocan.plates

import org.nanocan.file.ResultFile
import org.nanocan.file.ResultFileConfig

class Readout {

    static belongsTo = [plate: Plate]

    String typeOfReadout
    int wavelength
    String assayType
    static hasMany = [wells: WellReadout]
    ResultFile resultFile
    ResultFile resultImage
    ResultFile protocol

    static constraints = {
        resultImage nullable: true
        protocol nullable: true
        typeOfReadout inList: ['Fluorescence']
        assayType inList: ['Cell Titer Blue']
    }
}