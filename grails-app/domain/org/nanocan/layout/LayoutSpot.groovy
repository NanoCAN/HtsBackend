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

class LayoutSpot implements Comparable{

    static searchable = {
        root false
    }

    CellLine cellLine
    LysisBuffer lysisBuffer
    Dilution dilutionFactor
    Inducer inducer
    SpotType spotType
    Sample sample
    Treatment treatment
    NumberOfCellsSeeded numberOfCellsSeeded
    WellLayout wellLayout
    int replicate
    Plate plate

    int block
    int col
    int row

    static belongsTo = SlideLayout
    SlideLayout layout

    static constraints = {
        plate nullable: true
        wellLayout nullable: true
        numberOfCellsSeeded nullable: true
        cellLine nullable:  true
        lysisBuffer nullable: true
        dilutionFactor nullable: true
        inducer nullable: true
        sample nullable:  true
        spotType nullable:  true
        treatment nullable:  true
        replicate nullable: true
    }

    static mapping = {
        layout index: 'lspot_idx'
        block index: 'lspot_idx'
        col index: 'lspot_idx'
        row index: 'lspot_idx'
    }

    //makes samples sortable in order block -> column -> row
    public int compareTo(def other) {
        //first compare in which 12er this spot is
        def blockTab = (int) ((block-1) / 12)
        def otherBlockTab = (int) ((other.block-1) /12)

        if(blockTab < otherBlockTab) return -1
        else if(blockTab > otherBlockTab) return 1
        else
        {
            if(row < other.row) return -1
            else if(row > other.row) return 1
            else{
                if(block < other.block) return -1
                else if(block > other.block) return 1
                else{
                    if(col < other.col) return -1
                    else if(col > other.col) return 1
                    else return 0
                }
            }
        }
    }

    String toString()
    {
        "B/C/R: ${block}/${col}/${row}"
    }
}
