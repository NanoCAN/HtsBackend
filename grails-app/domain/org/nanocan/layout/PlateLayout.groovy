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

class PlateLayout implements Serializable{

    String name
    String format
    String uuid

    int cols
    int rows

    Date dateCreated
    Date lastUpdated

    Person createdBy
    Person lastUpdatedBy

    static constraints = {
        uuid nullable: true
        name blank: false, unique:  true, nullable: false
        format inList: ["96-well", "384-well", "1536-well"], blank: false, editable: false
    }

    static hasMany = [wells: WellLayout, plates: Plate]
    SortedSet wells

    def beforeInsert = {
        if(format == "96-well")
        {
            cols = 12
            rows = 8
        }
        else if(format == "384-well")
        {
            cols = 24
            rows = 16
        }
        else if(format == "1536-well")
        {
            cols = 48
            rows = 32
        }
    }

    def beforeUpdate = {
        beforeInsert()
    }

    String toString()
    {
        name
    }
}
