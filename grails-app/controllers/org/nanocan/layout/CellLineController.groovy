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

import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

@Secured(['ROLE_USER'])
class CellLineController {

    def openLabFrameworkService

    def scaffold = true

    def searchOpenLabFrameworkCellLineData(){
        def jsonList = openLabFrameworkService.searchCellLineData(params.term)
        if(jsonList == null){
            response.sendError(404); return
        }
        else render jsonList as JSON
    }

    def redirectToOpenLabFramework(){
        def redirectToUrl = openLabFrameworkService.redirectToCellLineData(params.id)
        if(!redirectToUrl)
        {
             flash.message = "Redirect URL could not be determined"
             redirect(action: "show", id: params.id)
        }
        else redirect(url: redirectToUrl)
    }

    def delete() {
        def cellLineInstance = CellLine.get(params.id)
        if (!cellLineInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'cellLine.label', default: 'CellLine'), params.id])
            redirect(action: "index")
            return
        }
        else if(WellLayout.findByCellLine(cellLineInstance) || LayoutSpot.findByCellLine(cellLineInstance)){
            flash.message = "CellLine can not be deleted as long as it is used."
            redirect(action: "show", id: params.id)
            return
        }

        try {
            cellLineInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'cellLine.label', default: 'CellLine'), params.id])
            redirect(action: "index")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'cellLine.label', default: 'CellLine'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
