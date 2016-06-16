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

import org.nanocan.plates.WellReadout

class ReadoutImportService {

    def nextStep
    def fileImportService

    /**
     * Main method
     */
    def processResultFile(def objectInstance, String sheetContent, def columnMap, int skipLines, String progressId, String format)
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

                try {
                    newWellReadout.measuredValue = Double.valueOf(currentLine[columnMap.measuredValue])
                } catch(NumberFormatException nfe){
                    newWellReadout.measuredValue = Double.NaN
                }
                //compute well position if necessary
                if(columnMap.wellPosition != null)
                {
                    String wellPosition = currentLine[columnMap.wellPosition]
                    wellPosition = wellPosition.replaceAll (/"/, '')

                    try{
                        int wellPositionNumeric = Integer.valueOf(wellPosition)

                        int numOfCols
                        if(format == "96-well") numOfCols = 16
                        else if(format == "384-well") numOfCols = 24
                        else if(format == "1536-well") numOfCols = 32

                        newWellReadout.col = ((wellPositionNumeric-1) % numOfCols) + 1
                        newWellReadout.row = ((wellPositionNumeric-1) / numOfCols) + 1
                    } catch(NumberFormatException nfe){ //in case its not an integer we assume its alphanumeric well names
                        newWellReadout.row = Character.getNumericValue(wellPosition.charAt(0))-9
                        newWellReadout.col = Integer.valueOf(wellPosition.substring(1))
                    }
                }

                objectInstance.addToWells(newWellReadout)
                wellReadouts << newWellReadout

            }catch(ArrayIndexOutOfBoundsException e)
            {
                log.debug "could not parse line, assuming the end is reached."
            }
        }

        //clean up
        scanner.close()

        if(progressId) nextStep = fileImportService.initializeProgressBar(wellReadouts, progressId)

        objectInstance.save(flush:true, failOnError: true)

        return objectInstance
    }

    public ArrayList<String> readoutProperties = ["wellPosition", "row", "column", "measuredValue"]

    def readSheet(filePath, sheet, minColRead, csvType, skipLines, columnSeparator, decimalSeparator, thousandSeparator, resultFileCfg){
        //read sheet / file

        def sheetContent = fileImportService.importFromFile(filePath, sheet, minColRead)

        //convert CSV2 to CSV:
        if (csvType == "CSV2") sheetContent = fileImportService.convertCSV2(sheetContent)

        //convert custom CSV format to standard CSV:
        else if (csvType == "custom") sheetContent = fileImportService.convertCustomCSV(sheetContent, columnSeparator, decimalSeparator, thousandSeparator)

        def header

        header = fileImportService.extractHeader(sheetContent, skipLines)

        //matching properties
        def matchingMap = createMatchingMap(resultFileCfg, header)

        [header: header, matchingMap: matchingMap, totalSkipLines: (++skipLines), sheetContent: sheetContent]
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
