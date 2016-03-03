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
package org.nanocan.security

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class PersonController {

    def userRole = Role.findByAuthority("ROLE_USER")
    def adminRole = Role.findByAuthority("ROLE_ADMIN")

    def mySecurityService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [personInstanceList: Person.list(params), personInstanceTotal: Person.count()]
    }

    def create() {
        [personInstance: new Person(params)]
    }

    def save() {
        boolean isAdmin = params.isAdmin
        params.remove(isAdmin)

        def personInstance = new Person(params)
        if (!personInstance.save(flush: true)) {

            render(view: "create", model: [personInstance: personInstance, isAdmin: isAdmin])
            return
        }

        PersonRole.create personInstance, userRole, true
        if(isAdmin) { PersonRole.create personInstance, adminRole, true }

		flash.message = message(code: 'default.created.message', args: [message(code: 'person.label', default: 'Person'), personInstance.id])
        redirect(action: "show", id: personInstance.id)
    }

    def show() {
        def personInstance = Person.get(params.id)
        if (!personInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'person.label', default: 'Person'), params.id])
            redirect(action: "index")
            return
        }

        def isAdmin = PersonRole.findByPersonAndRole(personInstance, adminRole) != null

        [personInstance: personInstance, isAdmin: isAdmin]
    }

    def edit() {
        def personInstance = Person.get(params.id)
        if (!personInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'person.label', default: 'Person'), params.id])
            redirect(action: "index")
            return
        }

        def isAdmin = PersonRole.findByPersonAndRole(personInstance, adminRole) != null

        [personInstance: personInstance, isAdmin: isAdmin]
    }

    def update() {
        def personInstance = Person.get(params.id)

        def isAdmin = params.isAdmin
        params.remove(isAdmin)

        if (!personInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'person.label', default: 'Person'), params.id])
            redirect(action: "index")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (personInstance.version > version) {
                personInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'person.label', default: 'Person')] as Object[],
                          "Another user has updated this Person while you were editing")
                render(view: "edit", model: [personInstance: personInstance])
                return
            }
        }

        personInstance.properties = params

        if (!personInstance.save(flush: true)) {
            render(view: "edit", model: [personInstance: personInstance])
            return
        }

        mySecurityService.updateAdminStatus(personInstance, isAdmin, adminRole)

		flash.message = message(code: 'default.updated.message', args: [message(code: 'person.label', default: 'Person'), personInstance.id])
        redirect(action: "show", id: personInstance.id)
    }

    def delete() {
        def personInstance = Person.get(params.id)
        if (!personInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'person.label', default: 'Person'), params.id])
            redirect(action: "index")
            return
        }

        try {
            PersonRole.findByPerson(personInstance).delete()

            personInstance.delete(flush: true)

			flash.message = message(code: 'default.deleted.message', args: [message(code: 'person.label', default: 'Person'), params.id])
            redirect(action: "index")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'person.label', default: 'Person'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
