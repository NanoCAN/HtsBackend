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
package org.nanocan.io

import org.apache.commons.io.FilenameUtils
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile
import org.nanocan.file.ResultFile

import static java.util.UUID.randomUUID

/**
 * Service takes files out of the request and persists them in the appropriate fashion.
 */
class FileUploadService {

    def grailsApplication

    /*
     * Creates the actual result file on the hard drive + a corresponding domain object
     */
    def createResultFile(resultFile, String type, String originalFileName)
    {
        if(resultFile instanceof CommonsMultipartFile){
            if(resultFile.empty){
                log.error "file was empty"
                return null
            }
            else originalFileName = resultFile.originalFilename
        }

        def uuid = randomUUID() as String
        String basePath = grailsApplication?.config?.upload?.directory?:""
        def filePath = basePath + uuid + "." + FilenameUtils.getExtension(originalFileName as String)

        if(resultFile instanceof CommonsMultipartFile) resultFile.transferTo( new File(filePath) )
        else resultFile.renameTo( new File(filePath) )

        def newResultFile = new ResultFile(fileType: type, fileName: (originalFileName as String), filePath: filePath, dateUploaded:  new Date())

        if(newResultFile.save(flush: true))
        {
            log.info "saving of file ${filePath} was successful"
            return newResultFile
        }

        else
        {
            log.warn "could not save file to ${filePath}."
            return null
        }
    }

    def makePathURLSafe(String filePath) {
        return FilenameUtils.getFullPath(filePath) + FilenameUtils.getBaseName(filePath).split("_")[0]
    }

    def getImagezoomTarget(def filePath) {
        return "imagezoom/" + getImagezoomFolder(filePath)
    }

    def getImagezoomFolder(def filePath) {
        return FilenameUtils.removeExtension(FilenameUtils.getName(makePathURLSafe(filePath)))
    }

    /**
     * Distinguishes between different input types and triggers persistence
     * @param request
     * @param params
     * @return
     */
    def dealWithFileUploads(def request, def objectInstance)
    {
        CommonsMultipartFile resultFile
        CommonsMultipartFile resultImage
        CommonsMultipartFile protocol

        /* if new files have been uploaded we take them instead */
        if(request instanceof MultipartHttpServletRequest)
        {
            MultipartHttpServletRequest mpr = (MultipartHttpServletRequest)request;

            resultFile = (CommonsMultipartFile) mpr.getFile("resultFileInput");
            resultImage = (CommonsMultipartFile) mpr.getFile("resultImageInput");
            protocol = (CommonsMultipartFile) mpr.getFile("protocolInput");

            if(resultFile && !resultFile.empty) objectInstance.resultFile = createResultFile(resultFile, "Result", null)

            if(resultImage && !resultImage.empty) objectInstance.resultImage = createResultFile(resultImage, "Image", null)

            if(protocol && !protocol.empty) objectInstance.protocol = createResultFile(protocol, "Protocol", null)
        }

        return objectInstance
    }
}
