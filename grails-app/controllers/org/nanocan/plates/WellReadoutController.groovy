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

import org.springframework.dao.DataIntegrityViolationException

class WellReadoutController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [wellReadoutInstanceList: WellReadout.list(params), wellReadoutInstanceTotal: WellReadout.count()]
    }

    def create() {
        [wellReadoutInstance: new WellReadout(params)]
    }

    def save() {
        def wellReadoutInstance = new WellReadout(params)
        if (!wellReadoutInstance.save(flush: true)) {
            render(view: "create", model: [wellReadoutInstance: wellReadoutInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'wellReadout.label', default: 'WellReadout'), wellReadoutInstance.id])
        redirect(action: "show", id: wellReadoutInstance.id)
    }

    def show() {
        def wellReadoutInstance = WellReadout.get(params.id)
        if (!wellReadoutInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'wellReadout.label', default: 'WellReadout'), params.id])
            redirect(action: "index")
            return
        }

        [wellReadoutInstance: wellReadoutInstance]
    }

    def edit() {
        def wellReadoutInstance = WellReadout.get(params.id)
        if (!wellReadoutInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'wellReadout.label', default: 'WellReadout'), params.id])
            redirect(action: "index")
            return
        }

        [wellReadoutInstance: wellReadoutInstance]
    }

    def update() {
        def wellReadoutInstance = WellReadout.get(params.id)
        if (!wellReadoutInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'wellReadout.label', default: 'WellReadout'), params.id])
            redirect(action: "index")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (wellReadoutInstance.version > version) {
                wellReadoutInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'wellReadout.label', default: 'WellReadout')] as Object[],
                        "Another user has updated this WellReadout while you were editing")
                render(view: "edit", model: [wellReadoutInstance: wellReadoutInstance])
                return
            }
        }

        wellReadoutInstance.properties = params

        if (!wellReadoutInstance.save(flush: true)) {
            render(view: "edit", model: [wellReadoutInstance: wellReadoutInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'wellReadout.label', default: 'WellReadout'), wellReadoutInstance.id])
        redirect(action: "show", id: wellReadoutInstance.id)
    }

    def delete() {
        def wellReadoutInstance = WellReadout.get(params.id)
        if (!wellReadoutInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'wellReadout.label', default: 'WellReadout'), params.id])
            redirect(action: "index")
            return
        }

        try {
            wellReadoutInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'wellReadout.label', default: 'WellReadout'), params.id])
            redirect(action: "index")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'wellReadout.label', default: 'WellReadout'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
