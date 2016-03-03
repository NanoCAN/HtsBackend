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

import org.nanocan.file.PlateResultFileConfig
import org.nanocan.file.ResultFile

class Readout implements Serializable{

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
