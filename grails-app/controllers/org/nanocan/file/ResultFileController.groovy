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
package org.nanocan.file

import grails.plugins.springsecurity.Secured
import grails.converters.JSON

@Secured(['ROLE_USER'])
class ResultFileController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def show = {

        def resultFileInstance = ResultFile.get(params.id)

        def file = new File(resultFileInstance.filePath)

        if (file.exists()) {
            response.setContentType("application/octet-stream")
            response.setHeader("Content-Disposition","attachment; filename=\"${resultFileInstance.fileName}\"")
            response.outputStream << file.bytes
            return
        }

        else
        {
            flash.message = "file does not exist!"
            redirect(action: "show", id: params.id)
        }
    }

    def ajaxResultImageFinder = {
        ajaxFileFinder("Image")
    }

    def ajaxResultFileFinder = {
        ajaxFileFinder("Result")
    }

    def ajaxProtocolFinder = {
        ajaxFileFinder("Protocol")
    }

    def ajaxFileFinder = { type ->

        def resultFilesFound = ResultFile.withCriteria {
            and{
                eq 'fileType', type
                or{
                    ilike 'fileName', params.term + '%'
                    ilike 'filePath', params.term + '%'
                }
            }
        }

        def resultFileSelectList = []

        resultFilesFound.each{
            def resultMap = [:]
            resultMap.put("id", it.id)
            resultMap.put("label", (it.fileName + " (" + it.dateUploaded.toLocaleString()+ ")"))
            resultMap.put("value", it.fileName)
            resultMap.put("uploaded", it.dateUploaded)
            resultFileSelectList.add(resultMap)
        }

        render (resultFileSelectList as JSON)
    }

    def scaffold = true
}