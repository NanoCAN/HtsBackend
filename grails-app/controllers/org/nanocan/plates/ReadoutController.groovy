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
import org.springframework.dao.DataIntegrityViolationException
import org.apache.commons.io.FilenameUtils
import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_USER'])
class ReadoutController {

    def fileUploadService
    def fileImportService
    def readoutImportService
    def progressService
    def unzipService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [readoutInstanceList: Readout.list(params), readoutInstanceTotal: Readout.count()]
    }

    def create() {
        [readoutInstance: new Readout(params)]
    }

    def save() {
        def readoutInstance = new Readout(params)

        //deal with file uploads
        readoutInstance = fileUploadService.dealWithFileUploads(request, readoutInstance)

        if (!readoutInstance.save(flush: true)) {
            render(view: "create", model: [readoutInstance: readoutInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'readout.label', default: 'Readout'), readoutInstance.id])
        redirect(action: "addReadoutData", id: readoutInstance.id)
    }

    def show() {
        def readoutInstance = Readout.get(params.id)
        if (!readoutInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'readout.label', default: 'Readout'), params.id])
            redirect(action: "index")
            return
        }

        [readoutInstance: readoutInstance]
    }

    def edit() {
        def readoutInstance = Readout.get(params.id)
        if (!readoutInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'readout.label', default: 'Readout'), params.id])
            redirect(action: "index")
            return
        }

        [readoutInstance: readoutInstance]
    }

    def update() {
        def readoutInstance = Readout.get(params.id)
        if (!readoutInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'readout.label', default: 'Readout'), params.id])
            redirect(action: "index")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (readoutInstance.version > version) {
                readoutInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'readout.label', default: 'Readout')] as Object[],
                        "Another user has updated this Readout while you were editing")
                render(view: "edit", model: [readoutInstance: readoutInstance])
                return
            }
        }

        readoutInstance.properties = params
        //deal with file uploads
        readoutInstance = fileUploadService.dealWithFileUploads(request, readoutInstance)

        if (!readoutInstance.save(flush: true)) {
            render(view: "edit", model: [readoutInstance: readoutInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'readout.label', default: 'Readout'), readoutInstance.id])
        redirect(action: "show", id: readoutInstance.id)
    }

    def delete() {
        def readoutInstance = Readout.get(params.id)
        if (!readoutInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'readout.label', default: 'Readout'), params.id])
            redirect(action: "index")
            return
        }

        try {
            readoutInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'readout.label', default: 'Readout'), params.id])
            redirect(action: "index")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'readout.label', default: 'Readout'), params.id])
            redirect(action: "show", id: params.id)
        }
    }

    def heatmap(){
        [readoutId: params.id]
    }

    def scatter(){
        [readoutId: params.id]
    }

    /* Add and delete spots, import from result file */
    def addReadoutData() {
        def readoutInstance = Readout.get(params.id)

        def index = 1

        def fileEnding = FilenameUtils.getExtension(readoutInstance.resultFile.filePath)

        def sheets = fileImportService.getSheets(readoutInstance).collect{
            [index: index++, name: it]
        }

        [readoutInstance: readoutInstance, configs: PlateResultFileConfig.list(), fileEnding: fileEnding, sheets: sheets]
    }

    final ArrayList<String> readoutProperties = ["wellPosition", "row", "column", "measuredValue"]

    private def readSheet(filePath, sheet, minColRead, csvType, skipLines, columnSeparator, decimalSeparator, thousandSeparator, resultFileCfg){
        //read sheet / file
        def sheetContent
        try{
            sheetContent = fileImportService.importFromFile(filePath, sheet, minColRead)
        }catch(IOException e)
        {
            render "<div class='message'>Could not read file</div>"
            return
        }

        //convert CSV2 to CSV:
        if (csvType == "CSV2") sheetContent = fileImportService.convertCSV2(sheetContent)

        //convert custom CSV format to standard CSV:
        else if (csvType == "custom") sheetContent = fileImportService.convertCustomCSV(sheetContent, columnSeparator, decimalSeparator, thousandSeparator)

        def header

        try{
            header = fileImportService.extractHeader(sheetContent, skipLines)
        }catch(NoSuchElementException e) {
            render "<div class='message'>Could not read header!</div>"
            return
        }

        //matching properties
        def matchingMap = readoutImportService.createMatchingMap(resultFileCfg, header)

        [header: header, matchingMap: matchingMap, totalSkipLines: (++skipLines), sheetContent: sheetContent]
    }

    def readInputFile(){
        def readoutInstance = Readout.get(params.id)

        //read config
        def resultFileCfg

        if(!params.config.equals("")){
            resultFileCfg = PlateResultFileConfig.get(params.config)
            readoutInstance.lastConfig = resultFileCfg
            readoutInstance.save()
        }
        def skipLines = resultFileCfg?.skipLines?:0
        def filePath = readoutInstance.resultFile.filePath

        if (params.skipLines == "on") skipLines = params.int("howManyLines")
        def result = readSheet(filePath, params.int("sheet")?:0, params.int("minColRead")?:1, params.csvType, skipLines, params.columnSeparator, params.decimalSeparator, params.thousandSeparator, resultFileCfg)

        //keeping content for later
        flash.totalSkipLines = result.totalSkipLines
        flash.sheetContent = result.sheetContent

        render view: "assignFields", model: [progressId: "pId${params.id}",
                                             header: result.header,
                                             readoutProperties: readoutProperties,
                                             matchingMap: result.matchingMap]
    }

    def processResultFile() {

        def columnMap = [:]

        params.keySet().each{
            if(it.toString().startsWith("column") && it.toString() != "columnSeparator") columnMap.put(params.get(it), Integer.parseInt(it.toString().split('_')[1]))
        }

        def readoutInstance = Readout.get(params.id)

        def progressId = "pId" + params.id

        progressService.setProgressBarValue(progressId, 0)

        if (readoutInstance.wells.size() > 0) {
            render "this readout instance already contains readout data. please delete them first!"
            progressService.setProgressBarValue(progressId, 100)
            return
        }

        def result = readoutImportService.processResultFile(readoutInstance, flash.sheetContent, columnMap, flash.totalSkipLines, progressId)

        progressService.setProgressBarValue(progressId, 100)

        if(!(result instanceof Readout)) render result

        else render "${readoutInstance.wells.size()} readout values have been added to the database and linked to this readout."
    }

    def zipOptions(){
        render template: "readoutDataForm", model: [fileEnding: params.fileEnding]
    }

    def createFromZipFileFlow = {

        beginState{
            action{
                [readoutInstance: new Readout()]
            }
            on("success").to "uploadZipFile"
        }

        uploadZipFile {
            on("upload").to "unzipFiles"
        }

        unzipFiles{
            action {
                // Grab readout settings
                def readoutInstance = new Readout(params)
                flow.assayType = readoutInstance.assayType
                flow.dateOfReadout = readoutInstance.dateOfReadout
                flow.typeOfReadout = readoutInstance.typeOfReadout

                // Grab file and check it exists
                def dataFile = request.getFile("zippedFile")
                if (dataFile.empty) {
                    flash.error = 'A file has to be chosen.'
                    render(view: 'createFromZipFile/createFromZippedFile')
                    return
                }
                // unpack zip file
                def unpacked = unzipService.unpack(dataFile.getInputStream())
                flow.unpacked = unpacked

                // get first file from zip to to read header
                def firstFile = unpacked.get(unpacked.keySet().first())
                flow.filePath = firstFile.path

                def index = 1
                flow.fileEnding = FilenameUtils.getExtension(firstFile.path)
                flow.sheets = fileImportService.getSheets(firstFile).collect {
                    [index: index++, name: it]
                }
            }
            on("success").to "fileSettings"
            on(Exception).to "handleError"
        }

        fileSettings{
            on("readHeader").to "readHeader"
        }

        readHeader{
            action{
                def skipLines = 0
                if (params.skipLines == "on") skipLines = params.int("howManyLines")
                flow.skipLines = skipLines

                def resultFileCfg
                flow.sheet = params.int("sheet")?:0
                flow.minColRead = params.int("minColRead")?:1
                flow.csvType = params.csvType
                flow.columnSeparator = params.columnSeparator
                flow.decimalSeparator = params.decimalSeparator
                flow.thousandSeparator = params.thousandSeparator

                def result = readSheet(flow.filePath, flow.sheet, flow.minColRead, flow.csvType, flow.skipLines,
                        flow.columnSeparator, flow.decimalSeparator, flow.thousandSeparator, resultFileCfg)

                flow.matchingMap = result.matchingMap
                flow.header = result.header
                flow.readoutProperties = readoutProperties
                flow.totalSkipLines = result.totalSkipLines
            }
            on("success").to "assignFields"
        }

        assignFields{
            on("process").to "importReadouts"
        }

        handleError{

        }

        importReadouts {
            action {
                def columnMap = [:]

                params.keySet().each {
                    if (it.toString().startsWith("column") && it.toString() != "columnSeparator") columnMap.put(params.get(it), Integer.parseInt(it.toString().split('_')[1]))
                }

                // Add transactional property
                Readout.withTransaction { status ->
                    try {
                        def unpacked = flow.unpacked
                        def listOfReadouts = []
                        unpacked.keySet().each { key ->
                            log.debug("-\nParsing " + key)

                            //extract barcode from filename
                            def currentFile = unpacked.get(key)
                            def fileBaseName = FilenameUtils.getBaseName(key)

                            //find matching plate
                            def plate = Plate.findByBarcode(fileBaseName)
                            if(!plate) throw new Exception("File ${key} could not be matched to an existing plate. Check filenames and try again. If necessary create new plates with barcodes matching the file base names (without extension).")

                            def readoutInstance = new Readout()
                            readoutInstance.assayType = flow.assayType
                            readoutInstance.dateOfReadout = flow.dateOfReadout
                            readoutInstance.typeOfReadout = flow.typeOfReadout
                            readoutInstance.plate = plate
                            readoutInstance.resultFile =  fileUploadService.createResultFile(currentFile, "Result", key)

                            def resultFileCfg // for later use

                            //read file content
                            def result = readSheet(readoutInstance.resultFile.filePath, flow.sheet, flow.minColRead, flow.csvType, flow.skipLines,
                                    flow.columnSeparator, flow.decimalSeparator, flow.thousandSeparator, resultFileCfg)

                            //Check if headers are identical
                            if(result.header != flow.header) throw new Exception("Header of file ${key} is not identical to header of first file, process aborted and rolled back.")

                            readoutImportService.processResultFile(readoutInstance, result.sheetContent, columnMap, flow.totalSkipLines, null)

                            listOfReadouts << readoutInstance
                            log.debug "Finished processing file ${key} successfully."
                        }
                    } catch (Exception e) {
                        //If an error occurs, rollback all changes
                        status.setRollbackOnly()
                        flow.error = e.message
                    }
                }
                if(flow.error){
                    throw new Exception(flow.error)
                }
            }
            on(Exception).to "handleError"
            on("success").to "showImportedReadouts"
        }

        showImportedReadouts{
            log.info "Parsing readouts completed successfully."
        }
    }
}
