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
import org.nanocan.project.Experiment
import org.nanocan.project.Project
import org.springframework.dao.DataIntegrityViolationException
import org.apache.commons.io.FilenameUtils
import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_USER'])
class ReadoutController {

    def fileUploadService
    def fileImportService
    def readoutImportService
    def progressService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index() {
        //deal with max
        if(!params.max && session.maxReadout) params.max = session.maxReadout
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        session.maxReadout = params.max

        //deal with offset
        params.offset = params.offset?:(session.offsetReadoutResult?:0)
        session.offsetReadoutResult = params.offset

        def readoutInstanceList
        def readoutInstanceListTotal

        if(session.experimentSelected)
        {
            readoutInstanceList = []
            Experiment.get(session.experimentSelected).plateLayouts.each{ playout ->
                Plate.findAllByPlateLayout(playout).each{ plate ->
                    readoutInstanceList.addAll(Readout.findAllByPlate(plate))
                }
            }
            readoutInstanceList.unique()
        }
        else if(session.projectSelected)
        {
            readoutInstanceList = []
            def experiments = Experiment.findAllByProject(Project.get(session.projectSelected as Long))
            experiments.each{
                it.plateLayouts.each { playout ->
                    Plate.findAllByPlateLayout(playout).each { plate ->
                        readoutInstanceList.addAll(Readout.findAllByPlate(plate))
                    }
                }
            }
            readoutInstanceList.unique()
        }
        else
        {
            readoutInstanceList = Readout.list(params)
            readoutInstanceListTotal = Readout.count()
        }

        if (session.experimentSelected || session.projectSelected)
        {
            readoutInstanceListTotal = readoutInstanceList?.size()?:0
            if (params.int('offset') > readoutInstanceListTotal) params.offset = 0

            if(readoutInstanceListTotal > 0)
            {
                int rangeMin = Math.min(readoutInstanceListTotal, params.int('offset'))
                int rangeMax = Math.min(readoutInstanceListTotal, (params.int('offset') + params.int('max')))

                readoutInstanceList = readoutInstanceList.asList()
                if(params.sort) readoutInstanceList = readoutInstanceList.sort{it[params.sort]}
                else readoutInstanceList.sort{ a,b -> a.id <=> b.id}
                if(params.order == "desc") readoutInstanceList = readoutInstanceList.reverse()

                readoutInstanceList = readoutInstanceList.subList(rangeMin, rangeMax)
            }
        }

        [readoutInstanceList: readoutInstanceList, readoutInstanceTotal: readoutInstanceListTotal]
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
        def readoutInstance = Readout.get(params.id)

        def levels

        if(readoutInstance.plate.format == "96-well") levels = '["H","G","F","E","D","C","B","A"]'
        else if(readoutInstance.plate.format == "384-well") levels = '["L","K","J","I","H","G","F","E","D","C","B","A"]'
        else if(readoutInstance.plate.format == "1536-well") levels = '["X","W","V","U","T","S","R","Q","P","O","N","M","L","K","J","I","H","G","F","E","D","C","B","A"]'
        [readoutId: params.id, levels: levels]
    }

    def scatter(){
        [readoutId: params.id]
    }

    def customCSV(){
        if(params.selectedType == "custom") render template: "customCSV"
        else render ""
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

        def result
        try {
            result = readoutImportService.readSheet(filePath, params.int("sheet") ?: 0, params.int("minColRead") ?: 1, params.csvType, skipLines, params.columnSeparator, params.decimalSeparator, params.thousandSeparator, resultFileCfg)
        } catch(IOException e)
        {
            render "<div class='message'>Could not read file</div>"
            return
        } catch(NoSuchElementException nsee){
            render "<div class='message'>Could not read header</div>"
            return
        }
        //keeping content for later
        flash.totalSkipLines = result.totalSkipLines
        flash.sheetContent = result.sheetContent

        render view: "assignFields", model: [progressId: "pId${params.id}",
                                             header: result.header,
                                             readoutProperties: readoutImportService.readoutProperties,
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

        def result = readoutImportService.processResultFile(readoutInstance, flash.sheetContent, columnMap, flash.totalSkipLines, progressId, readoutInstance.plate.format)

        progressService.setProgressBarValue(progressId, 100)

        if(!(result instanceof Readout)) render result

        else render "${readoutInstance.wells.size()} readout values have been added to the database and linked to this readout."
    }

}
