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

class Sample implements Serializable{

    static searchable = true
    String name
    String type
    String target
    String color
    boolean control
    String controlType
    String uuid
    String sampleGroupA
    String sampleGroupADescription
    String sampleGroupB
    String sampleGroupBDescription
    String sampleGroupC
    String sampleGroupCDescription

    static hasMany = [identifiers: Identifier]

    static constraints = {
        uuid nullable: true
        sampleGroupA nullable: true
        sampleGroupADescription nullable: true
        sampleGroupB nullable: true
        sampleGroupBDescription nullable: true
        sampleGroupC nullable: true
        sampleGroupCDescription nullable: true
        name unique: true
        controlType inList: ["positive", "negative", "transfection", "kill"], nullable: true
        type inList: ["sgRNA", "shRNA", "siRNA", "miRNA inhibitor", "miRNA mimic", "compound", "unknown", "cell-line", "tissue"]
        target nullable: true
        color unique:  false, validator:  {val, obj -> (val != "#ffffff" && val != "#e0e0e0")}, nullable: false
    }

    String toString()
    {
        name
    }
}
