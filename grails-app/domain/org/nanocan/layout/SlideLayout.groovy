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
package org.nanocan.layout

import org.nanocan.plates.Plate
import org.nanocan.security.Person

class SlideLayout implements Serializable{

    Date dateCreated
    Date lastUpdated

    Person createdBy
    Person lastUpdatedBy
    
    String title
    int columnsPerBlock
    int rowsPerBlock
    int numberOfBlocks
    Integer blocksPerRow
    String depositionPattern
    List sourcePlates
    ExtractionHead extractionHead

    static hasMany = [sampleSpots: LayoutSpot, sourcePlates: Plate]

    SortedSet sampleSpots

    static constraints = {
        extractionHead nullable:true
        sourcePlates nullable: true
        title unique: true, blank: false
        columnsPerBlock min:  1
        rowsPerBlock min:  1
        numberOfBlocks min:  1
        blocksPerRow nullable: true, min: 1
        depositionPattern validator: {
            if(!(it ==~ /\[([1-9]+,)*+[1-9]+\]/)) return ("default.deposition.pattern.mismatch")
        }
    }
    
    String toString(){
        title
    }
}
