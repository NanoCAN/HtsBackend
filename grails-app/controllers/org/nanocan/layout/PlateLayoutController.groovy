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

import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException
import org.nanocan.project.Project
import org.nanocan.project.Experiment

@Secured(['ROLE_USER'])
class PlateLayoutController {

    def plateLayoutService
    def progressService
    def experimentService
    def springSecurityService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def showWellTooltip(){

        render template: "wellPreview", model: [wellLayoutInstance: WellLayout.get(params.long("id"))]
    }

    def index() {
        //deal with max
        if(!params.max && session.maxPlateLayout) params.max = session.maxPlateLayout
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        session.maxPlateLayout = params.max

        //deal with offset
        params.offset = params.offset?:(session.offsetPlateLayout?:0)
        session.offsetPlateLayout = params.offset

        def plateLayoutInstanceList
        def plateLayoutInstanceListTotal

        if (session.experimentSelected)
        {
            plateLayoutInstanceList = Experiment.get(session.experimentSelected).plateLayouts
        }
        else if(session.projectSelected)
        {
            plateLayoutInstanceList = []
            Experiment.findAllByProject(Project.get(session.projectSelected as Long))?.each{plateLayoutInstanceList.addAll(it.plateLayouts)}
        }
        else
        {
            plateLayoutInstanceList = PlateLayout.list(params)
            plateLayoutInstanceListTotal = PlateLayout.count()
        }

        //create subset of list
        if (session.experimentSelected || session.projectSelected)
        {
            plateLayoutInstanceListTotal = plateLayoutInstanceList?.size()?:0
            //fix offset when necessary
            if(params.int('offset') >= plateLayoutInstanceListTotal) params.offset = 0

            if(plateLayoutInstanceListTotal > 0)
            {
                int rangeMin = Math.min(plateLayoutInstanceListTotal, params.int('offset'))
                int rangeMax = Math.min(plateLayoutInstanceListTotal, (params.int('offset') + params.int('max')))

                plateLayoutInstanceList = plateLayoutInstanceList.asList()
                if(params.sort) plateLayoutInstanceList = plateLayoutInstanceList.sort{it[params.sort]}
                else plateLayoutInstanceList.sort{ a,b -> a.id <=> b.id}
                if(params.order == "desc") plateLayoutInstanceList = plateLayoutInstanceList.reverse()

                plateLayoutInstanceList = plateLayoutInstanceList.subList(rangeMin, rangeMax)
            }
        }

        [plateLayoutInstanceList: plateLayoutInstanceList, plateLayoutInstanceTotal: plateLayoutInstanceListTotal]
    }

    def create() {
        [plateLayoutInstance: new PlateLayout(params), experiments: Experiment.list()]
    }

    def save() {

        params.createdBy = springSecurityService.currentUser
        params.lastUpdatedBy = springSecurityService.currentUser

        def plateLayoutInstance = new PlateLayout(params)
        if (!plateLayoutInstance.save(flush: true)) {
            render(view: "create", model: [plateLayoutInstance: plateLayoutInstance])
            return
        }

        plateLayoutService.createWellLayouts(plateLayoutInstance)
        experimentService.addToExperiment(plateLayoutInstance, params.experimentsSelected)

		flash.message = message(code: 'default.created.message', args: [message(code: 'plateLayout.label', default: 'PlateLayout'), plateLayoutInstance.id])
        redirect(action: "show", id: plateLayoutInstance.id)
    }

    def show() {
        def plateLayoutInstance = PlateLayout.get(params.id)
        if (!plateLayoutInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'plateLayout.label', default: 'PlateLayout'), params.id])
            redirect(action: "index")
            return
        }
        flash.message = flash.message

        redirect(action: "editAttributes", id:  plateLayoutInstance.id, params: params << [sampleProperty: "cellLine"])
    }

    def edit() {
        def plateLayoutInstance = PlateLayout.get(params.id)
        if (!plateLayoutInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'plateLayout.label', default: 'PlateLayout'), params.id])
            redirect(action: "index")
            return
        }

        def experiments = experimentService.findExperiment(plateLayoutInstance)

        [plateLayoutInstance: plateLayoutInstance, experiments: Experiment.list(), selectedExperiments: experiments]
    }

    def addToExperiment(){
        experimentService.addToExperiment(PlateLayout.get(params.id), [params.long("experiment")])
        redirect(action: "editAttributes", id: params.id)
    }

    def removeFromExperiment(){
        experimentService.removeFromExperiment(PlateLayout.get(params.id), [params.long("experiment")])
        redirect(action: "editAttributes", id: params.id)
    }

    def update() {
        def plateLayoutInstance = PlateLayout.get(params.id)
        if (!plateLayoutInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'plateLayout.label', default: 'PlateLayout'), params.id])
            redirect(action: "index")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (plateLayoutInstance.version > version) {
                plateLayoutInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'plateLayout.label', default: 'PlateLayout')] as Object[],
                          "Another user has updated this PlateLayout while you were editing")
                render(view: "edit", model: [plateLayoutInstance: plateLayoutInstance])
                return
            }
        }
        params.lastUpdatedBy = springSecurityService.currentUser

        plateLayoutInstance.properties = params

        if (!plateLayoutInstance.save(flush: true)) {
            render(view: "edit", model: [plateLayoutInstance: plateLayoutInstance])
            return
        }

        experimentService.updateExperiments(plateLayoutInstance, params.experimentsSelected)

		flash.message = message(code: 'default.updated.message', args: [message(code: 'plateLayout.label', default: 'PlateLayout'), plateLayoutInstance.id])
        redirect(action: "show", id: plateLayoutInstance.id)
    }

    def delete() {
        def plateLayoutInstance = PlateLayout.get(params.id)
        if (!plateLayoutInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'plateLayout.label', default: 'PlateLayout'), params.id])
            redirect(action: "index")
            return
        }
        else if(plateLayoutInstance.plates){
            flash.message = "PlateLayout can not be deleted as long as there are associated plates."
            redirect(action: "show", id: params.id)
            return
        }

        try {
            experimentService.updateExperiments(plateLayoutInstance, [])
            plateLayoutInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'plateLayout.label', default: 'PlateLayout'), params.id])
            redirect(action: "index")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'plateLayout.label', default: 'PlateLayout'), params.id])
            redirect(action: "show", id: params.id)
        }
    }

    /* methods for changing attributes */
    def sampleList(){
        def plateLayoutInstance = PlateLayout.get(params.id)
        def samples = plateLayoutInstance.wells.collect{it.sample}

        [samples: samples.unique()]
    }

    def editAttributes(){
        def plateLayoutInstance = PlateLayout.get(params.id)
        def selectedExperiments = experimentService.findExperiment(plateLayoutInstance)
        def experiments = Experiment.list()
        experiments.removeAll(selectedExperiments)

        [plateLayout:  plateLayoutInstance, wells: plateLayoutInstance.wells, plates: plateLayoutInstance.plates, experiments: experiments, selectedExperiments: selectedExperiments, sampleProperty: params.sampleProperty?:"cellLine"]
    }

    def showAttributes(){
        def plateLayoutInstance = PlateLayout.get(params.id)

        [plateLayout:  plateLayoutInstance, wells: plateLayoutInstance.wells, sampleProperty: params.sampleProperty]
    }

    def updateWellProperty()
    {
        def wellProp = params.wellProperty

        def plateLayout = params.plateLayout

        params.remove("action")
        params.remove("controller")
        params.remove("wellProperty")
        params.remove("plateLayout")

        if(params.size() == 0) render "Nothing to do"

        plateLayoutService.updateWellProperties(params, wellProp, plateLayout)

        progressService.setProgressBarValue("update${plateLayout}", 100)
        render "Save successful"
    }

    def createLayoutCopy() {
        def plateLayoutInstance = PlateLayout.get(params.id)
        def selectedExperiments = experimentService.findExperiment(plateLayoutInstance)
        def experiments = Experiment.list()

        if(PlateLayout.findByName(params.name)){
            flash.message = "Could not create copy: Name already in use"
            render(view: "editAttributes", model: [plateLayout: plateLayoutInstance, wells: plateLayoutInstance.wells,
                                                   experiments: experiments, selectedExperiments: selectedExperiments,
                                                   sampleProperty: params.sampleProperty])
            return
        }

        PlateLayout newPlateLayout = plateLayoutService.copyPlateLayout(plateLayoutInstance, params.name)
        newPlateLayout.lastUpdatedBy = springSecurityService.currentUser
        newPlateLayout.createdBy = springSecurityService.currentUser

        experiments.removeAll(selectedExperiments)

        if(newPlateLayout.save(flush: true)){
            //also add to same experiments
            selectedExperiments.each{ experimentService.addToExperiment(newPlateLayout, it) }
            flash.message = "Copy created successfully. Be aware: you are now working on the copy!"
            render (view:  "editAttributes", model: [plateLayout: newPlateLayout, wells: newPlateLayout.wells,
                                                     experiments: experiments, selectedExperiments: selectedExperiments,
                                                     sampleProperty: params.sampleProperty])
        }
        else{
            flash.message = "Could not create copy: " + newPlateLayout.errors.toString()
            render(view: "editAttributes", model: [plateLayout: plateLayoutInstance, wells: plateLayoutInstance.wells,
                                                   experiments: experiments, selectedExperiments: selectedExperiments,
                                                   sampleProperty: params.sampleProperty])
        }
    }

    def clearProperty() {
        def plateLayoutInstance = PlateLayout.get(params.id)
        plateLayoutInstance.wells.each{
            it."${params.sampleProperty}" = null
            if(!it.save(flush:true, failOnError: true))
                flash.message = "Could not clear sheet"
        }
        render(view: "editAttributes", model: [plateLayout: plateLayoutInstance, wells: plateLayoutInstance.wells, sampleProperty: params.sampleProperty])
    }

    def importLayout() {

    }
}
