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
import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

@Secured(['ROLE_USER'])
class SampleController {

    def scaffold = true

    def delete() {
        def sampleInstance = Sample.get(params.id)
        if (!sampleInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'sample.label', default: 'Sample'), params.id])
            redirect(action: "index")
            return
        }
        else if(WellLayout.findBySample(sampleInstance) || LayoutSpot.findBySample(sampleInstance)){
            flash.message = "Sample can not be deleted as long as it is used."
            redirect(action: "show", id: params.id)
            return
        }

        try {
            sampleInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'sample.label', default: 'Sample'), params.id])
            redirect(action: "index")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'sample.label', default: 'Sample'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
    
    def listControls(){

        def controls = Sample.findAllByControl(true)

        render(view: "index", model : [sampleInstanceList: controls, sampleInstanceTotal: controls.size() ])
    }

    def ajaxSampleFinder = {
        def samplesFound = Sample.withCriteria {
            and{
                isEmpty("identifiers")

                or
                {
                    ilike 'name', params.term + '%'
                    ilike 'target', params.term + '%'
                }
            }
        }

        def moreSamplesFound = Sample.withCriteria {
            or
            {
                ilike 'name', params.term + '%'
                ilike 'target', params.term + '%'
                identifiers{
                   or
                   {
                     ilike 'name', params.term + '%'
                     ilike 'accessionNumber', params.term + '%'
                   }
                }
            }
        }

        samplesFound.addAll(moreSamplesFound)

        def sampleSelectList = []

        samplesFound.each{
            def resultMap = [:]
            resultMap.put("id", it.id)
            resultMap.put("label", it.name)
            resultMap.put("value", it.name)
            resultMap.put("colour", it.color)
            sampleSelectList.add(resultMap)
        }

        render (sampleSelectList as JSON)
    }

    def legendSampleSelected = {

        render "<script>alert('test');<script>"
    }
}
