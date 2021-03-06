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

import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.openxml4j.opc.PackageAccess
import org.apache.poi.xssf.model.StylesTable

import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable
import org.apache.poi.xssf.eventusermodel.XSSFReader
import org.nanocan.excelimport.MyXLSXHandler

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.nanocan.excelimport.MyXLStoCSVConverter
import org.apache.commons.io.FilenameUtils
import org.xml.sax.ContentHandler
import org.xml.sax.InputSource
import org.xml.sax.XMLReader

import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

/**
 * Created with IntelliJ IDEA.
 * User: markus
 * Date: 2/7/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
class XlsxImportService {


    def getSheets(filePath){

        def fileEnding = FilenameUtils.getExtension(filePath)
        def sheets = []

        if(fileEnding == "xlsx")
        {
            sheets = processXLSX(filePath, sheets)
        }

        else if (fileEnding == "xls"){

            def workbook
                try{
                    workbook = WorkbookFactory.create(new File(filePath))
                }catch(org.apache.poi.hssf.OldExcelFormatException oefe){
                    def escapedFilePath =  filePath.replace(" ", "\\ ");
                    println escapedFilePath
                 //if we have to deal with very old excel files, try to convert it using ssconvert (needs to be installed of course)
                    Process p = Runtime.getRuntime().exec("ssconvert ${escapedFilePath} ${escapedFilePath}x")
                    p.waitFor()
                    //resultFile.filePath = "${filePath}x"
                    //resultFile.save(flush:true)
                    //new File(filePath).delete()
                    if(new File("${filePath}x").exists())
                        return processXLSX("${filePath}x", sheets)
                    else return null
                }
            def numOfSheets = workbook.getNumberOfSheets()
            for (int i = 0; i < numOfSheets; i++)
            {
                sheets << workbook.getSheetName(i)
            }
        }

        return sheets
    }

    private processXLSX(filePath, ArrayList sheets) {
        OPCPackage pkg = OPCPackage.open(filePath, PackageAccess.READ);

        XSSFReader r = new XSSFReader(pkg)
        XSSFReader.SheetIterator iterator = (XSSFReader.SheetIterator) r.getSheetsData()

        while (iterator.hasNext()) {
            iterator.next()
            sheets << iterator.getSheetName()
        }
        return sheets
    }

    def parseXLSXSheetToCSV(filePath, sheetIndex, minColNum) {

        OPCPackage xlsxPackage = OPCPackage.open(filePath, PackageAccess.READ)

        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(xlsxPackage)
        XSSFReader reader = new XSSFReader(xlsxPackage)
        StylesTable styles = reader.getStylesTable()
        def instream = reader.getSheet("rId" + sheetIndex)

        InputSource sheetSource = new InputSource(instream)
        SAXParserFactory saxFactory = SAXParserFactory.newInstance()
        SAXParser saxParser = saxFactory.newSAXParser()
        XMLReader sheetParser = saxParser.getXMLReader()

        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        PrintStream ps = new PrintStream(baos)

        ContentHandler handler = new MyXLSXHandler(styles, strings, minColNum, ps)
        sheetParser.setContentHandler(handler)
        sheetParser.parse(sheetSource)

        instream.close()
        ps.close()

        def result = baos.toString()
        baos.close()

        return result
    }

    def parseXLSSheetToCSV(filePath, sheetIndex, minColNum) {

        def fileInputStream = new FileInputStream(filePath)

        def fileSystem = new POIFSFileSystem(fileInputStream)

        def baos = new ByteArrayOutputStream()
        def ps = new PrintStream(baos)

        def converter = new MyXLStoCSVConverter(fileSystem, ps, minColNum, Integer.parseInt(sheetIndex))
        converter.process()

        fileInputStream.close()
        ps.close()

        def result = baos.toString()
        baos.close()

        return result
    }

}









