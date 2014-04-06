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
package org.nanocan.io

import groovy.sql.Sql
import org.nanocan.plates.WellReadout


class ReadoutImportService {

    def nextStep
    def fileImportService
    /**
     * Main method
     */
    def processResultFile(def objectInstance, String sheetContent, def columnMap, int skipLines, String progressId)
    {
        Scanner scanner = new Scanner(sheetContent)

        //skip lines including header this time
        for(int i = 0; i < skipLines; i++)
        {
            if(scanner.hasNextLine()) scanner.nextLine()
        }

        def wellReadouts = []

        while(scanner.hasNextLine())
        {
            def currentLine = scanner.nextLine()

            currentLine = currentLine.split(',')

            try
            {
                def newWellReadout = new WellReadout()

                if(columnMap.col) newWellReadout.col = Integer.valueOf(currentLine[columnMap.col])
                if(columnMap.row) newWellReadout.row = Integer.valueOf(currentLine[columnMap.row])

                newWellReadout.measuredValue = Double.valueOf(currentLine[columnMap.measuredValue])

                //compute well position if necessary
                if(columnMap.wellPosition != null)
                {
                    def wellPosition = currentLine[columnMap.wellPosition]
                    newWellReadout.row = Character.getNumericValue(wellPosition.toString().charAt(0))-9
                    newWellReadout.col = Integer.valueOf(wellPosition.toString().substring(1, wellPosition.toString().length()))
                }

                objectInstance.addToWells(newWellReadout)
                wellReadouts << newWellReadout

            }catch(ArrayIndexOutOfBoundsException e)
            {
                log.info "could not parse line, assuming the end is reached."
            }
        }

        //clean up
        scanner.close()

        nextStep = fileImportService.initializeProgressBar(wellReadouts, progressId)

        objectInstance.save(flush:true)

        return objectInstance
    }

    def createMatchingMap(def resultFileCfg, List<String> header) {
        def matchingMap = [:]

        if (resultFileCfg) {

            for (String colName : header) {
                def trimmedColName = colName

                //remove leading and tailing quote
                if (colName.startsWith("\"") && colName.endsWith("\""))
                    trimmedColName = colName.substring(1, colName.length() - 1);

                switch (trimmedColName) {
                    case resultFileCfg.rowCol:
                        matchingMap.put(colName, "row")
                        break
                    case resultFileCfg.columnCol:
                        matchingMap.put(colName, "col")
                        break
                    case resultFileCfg.fgCol:
                        matchingMap.put(colName, "measuredValue")
                        break
                }
            }
        }
        matchingMap
    }
}
