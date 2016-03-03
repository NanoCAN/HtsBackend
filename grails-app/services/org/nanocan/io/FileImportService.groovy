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

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang.StringUtils

/**
 * This service handles the extraction from spot information from an excel sheet using the excel-import plugin.
 * The spots are persisted in the database using groovy sql (for performance reasons)
 */
class FileImportService {

    //dependencies
    def progressService
    def dataSourceUnproxied
    def grailsApplication
    def xlsxImportService

    /*
    * Some global variables
    */

    //global variables for progress bar
    def currentPercent = 0
    def onePercent

    /**
     * Get name of sheets of an excel file
     * @param slideInstance
     * @return
     */
    def getSheets(def objectInstance)
    {
        if(objectInstance instanceof File)
            return xlsxImportService.getSheets(objectInstance.path)
        else return xlsxImportService.getSheets(objectInstance.resultFile.filePath)
    }

    def convertCSV2(String content)
    {
        content = StringUtils.replace(content, ".", "")
        content = StringUtils.replace(content, ",", ".")
        content = StringUtils.replace(content, ";", ",")

        return content
    }

    def convertCustomCSV(String content, String columnSeparator, String decimalSeparator, String thousandSeparator)
    {
        switch(columnSeparator){
        case "comma":
            columnSeparator = ","
            break
        case "semicolon":
            columnSeparator = ";"
            break
        case "tab":
            columnSeparator = "\t"
            break
        }

        //remove thousand separator
        if(thousandSeparator != "")
        content = StringUtils.replace(content, thousandSeparator, "")

        //temporarily substitute column separator with tab to avoid comma confusion with decimal separator
        if(columnSeparator != "\t")
        {
            content = StringUtils.replace(content, columnSeparator, "\t")
        }

        //replace decimal separator
        content = StringUtils.replace(content, decimalSeparator, ".")

        //(re-)substitute column separator
        content = StringUtils.replace(content, "\t", ",")

        return (content)
    }

    /**
     * Skip lines and read header, then parse it to array
     * @param content
     * @return
     */
    def extractHeader(def content, def skipLines)
    {
        Scanner scanner = new Scanner(content)

        //skipping lines

        for( int i = 0; i < skipLines; i++ )
        {
            if(scanner.hasNext()) scanner.nextLine()
        }

        //reading and parsing header
        def header = scanner.nextLine()
        scanner.close()

        header = header.split(',')
        header = Arrays.asList(header)

        return (header)
    }


    /**
     * Import excel file using a fileinputstream
     * ResultFileConfig is a map of column letters to domain class properties, e.g. Signal is in column F, ...
     */
    def importFromFile(String filePath, int sheetIndex, int minNumOfCols) {

        def fileEnding = FilenameUtils.getExtension(filePath)

        if(fileEnding == "xlsx")
            return xlsxImportService.parseXLSXSheetToCSV(filePath, sheetIndex.toString(), minNumOfCols)
        else if(fileEnding == "xls")
            return xlsxImportService.parseXLSSheetToCSV(filePath, sheetIndex.toString(), minNumOfCols)
        else {
            return FileUtils.readFileToString(new File(filePath))
        }
    }

    //keep track of the progress, but update only every 1% to reduce the overhead
    def initializeProgressBar(objectList, progressId){
        progressService.setProgressBarValue(progressId, 0)
        def sizeOfList = objectList.size()

        onePercent = (int) (sizeOfList / 100)
        return onePercent
    }

    def updateProgressBar(nextStep, currentSpotIndex, progressId){
        if(currentSpotIndex == nextStep)
        {
            nextStep += onePercent
            progressService.setProgressBarValue(progressId, currentPercent++)
        }

        return nextStep
    }

    def mapToLayoutColumn(col, deposLength) {
        //match layout column to actual column
        int layoutColumn = (((col as Integer) - 1) / deposLength)
        //we don't want to start with zero
        layoutColumn++
        return layoutColumn
    }
}
