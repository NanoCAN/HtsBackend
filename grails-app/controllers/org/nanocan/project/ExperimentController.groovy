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
package org.nanocan.project

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured

@Secured(['ROLE_USER'])
class ExperimentController {

    def springSecurityService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [experimentInstanceList: Experiment.list(params), experimentInstanceTotal: Experiment.count()]
    }

    def create() {

        [experimentInstance: new Experiment(params)]
    }

    def save() {
        params.createdBy = springSecurityService.currentUser
        params.lastUpdatedBy = springSecurityService.currentUser

        def experimentInstance = new Experiment(params)
        if (!experimentInstance.save(flush: true)) {
            render(view: "create", model: [experimentInstance: experimentInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'experiment.label', default: 'Experiment'), experimentInstance.id])
        if (flash.backAction) redirect(controller: flash.backController, action: flash.backAction, params: flash.backParams)
        else redirect(action: "show", id: experimentInstance.id)
    }

    def show() {
        def experimentInstance = Experiment.get(params.id)
        if (!experimentInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'experiment.label', default: 'Experiment'), params.id])
            redirect(action: "list")
            return
        }

        [experimentInstance: experimentInstance]
    }

    /*
    * Add a parameter to session to keep track of the selected experiment
    */
    def updateSelectedExperiment() {
        session.experimentSelected = params.experimentSelect

        redirect(url: params.returnPage, absolute: false)
    }

    def edit() {
        def experimentInstance = Experiment.get(params.id)
        if (!experimentInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'experiment.label', default: 'Experiment'), params.id])
            redirect(action: "list")
            return
        }

        [experimentInstance: experimentInstance]
    }

    def update() {
        def experimentInstance = Experiment.get(params.id)
        if (!experimentInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'experiment.label', default: 'Experiment'), params.id])
            redirect(action: "list")
            return
        }

        params.lastUpdatedBy = springSecurityService.currentUser

        if (params.version) {
            def version = params.version.toLong()
            if (experimentInstance.version > version) {
                experimentInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'experiment.label', default: 'Experiment')] as Object[],
                        "Another user has updated this Experiment while you were editing")
                render(view: "edit", model: [experimentInstance: experimentInstance])
                return
            }
        }

        experimentInstance.properties = params

        if (!experimentInstance.save(flush: true)) {
            render(view: "edit", model: [experimentInstance: experimentInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'experiment.label', default: 'Experiment'), experimentInstance.id])
        redirect(action: "show", id: experimentInstance.id)
    }

    def delete() {
        def experimentInstance = Experiment.get(params.id)
        if (!experimentInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'experiment.label', default: 'Experiment'), params.id])
            redirect(action: "list")
            return
        }

        try {
            experimentInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'experiment.label', default: 'Experiment'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'experiment.label', default: 'Experiment'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
