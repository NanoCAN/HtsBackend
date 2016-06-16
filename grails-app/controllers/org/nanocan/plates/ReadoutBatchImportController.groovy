package org.nanocan.plates

import org.apache.commons.io.FilenameUtils

class ReadoutBatchImportController {

    def unzipService
    def fileUploadService
    def fileImportService
    def readoutImportService
    def progressService

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
                flow.assay = readoutInstance.assay
                flow.dateOfReadout = readoutInstance.dateOfReadout

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

                // id for progress bar
                flow.uuid = UUID.randomUUID().toString()

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

                def result
                try{
                    result = readoutImportService.readSheet(flow.filePath, flow.sheet, flow.minColRead, flow.csvType, flow.skipLines,
                            flow.columnSeparator, flow.decimalSeparator, flow.thousandSeparator, resultFileCfg)
                }catch(IOException e)
                {
                    flash.error = "Could not read file"
                    render(view: 'createFromZipFile/createFromZippedFile')
                    return
                } catch(NoSuchElementException nsee){
                    flash.error = "Could not read header"
                    render(view: 'createFromZipFile/createFromZippedFile')
                    return
                }

                flow.matchingMap = result.matchingMap
                flow.header = result.header
                flow.readoutProperties = readoutImportService.readoutProperties
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
                        def importUuid = flow.uuid

                        int numOfReadouts = unpacked.keySet().size()

                        unpacked.keySet().eachWithIndex { key, count ->
                            log.debug("-\nParsing " + key)

                            progressService.setProgressBarValue(importUuid, count / numOfReadouts * 100)

                            //extract barcode from filename
                            def currentFile = unpacked.get(key)
                            def fileBaseName = FilenameUtils.getBaseName(key)

                            //find matching plate
                            def plate = Plate.findByBarcode(fileBaseName)
                            if(!plate) throw new Exception("File ${key} could not be matched to an existing plate. Check filenames and try again. If necessary create new plates with barcodes matching the file base names (without extension).")

                            def readoutInstance = new Readout()
                            readoutInstance.assay = flow.assay
                            readoutInstance.dateOfReadout = flow.dateOfReadout
                            readoutInstance.plate = plate
                            readoutInstance.resultFile =  fileUploadService.createResultFile(currentFile, "Result", key)

                            def resultFileCfg // for later use

                            //read file content
                            def result = readoutImportService.readSheet(readoutInstance.resultFile.filePath, flow.sheet, flow.minColRead, flow.csvType, flow.skipLines,
                                    flow.columnSeparator, flow.decimalSeparator, flow.thousandSeparator, resultFileCfg)

                            //Check if headers are identical
                            if(result.header != flow.header) throw new Exception("Header of file ${key} is not identical to header of first file, process aborted and rolled back.")

                            readoutImportService.processResultFile(readoutInstance, result.sheetContent, columnMap, flow.totalSkipLines, null, plate.format)

                            listOfReadouts << readoutInstance
                            log.debug "Finished processing file ${key} successfully."
                        }
                        flow.listOfReadouts = listOfReadouts.sort{it.id}
                    } catch (Exception e) {
                        //If an error occurs, rollback all changes
                        status.setRollbackOnly()
                        flow.error = e.message
                    }
                }
                progressService.setProgressBarValue(flow.uuid, 100)

                if(flow.error){
                    throw new Exception(flow.error)
                    return
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
