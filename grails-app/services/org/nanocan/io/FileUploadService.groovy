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

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile
import org.nanocan.file.ResultFile

/**
 * Service takes files out of the request and persists them in the appropriate fashion.
 */
class FileUploadService {

    def imageConvertService
    def grailsApplication

    /*
     * Creates the actual result file on the hard drive + a corresponding domain object
     */
    def createResultFile(def resultFile, String type)
    {
        if(!resultFile.empty) {

            def currentDate = new java.util.Date()
            long timestamp = currentDate.getTime()
            String basePath = grailsApplication.config?.upload?.directory?:""
            def filePath = basePath + timestamp.toString() + "_" + resultFile.originalFilename
            resultFile.transferTo( new File(filePath) )

            def newResultFile = new ResultFile(fileType: type, fileName: (resultFile.originalFilename as String), filePath: filePath, dateUploaded:  currentDate as Date)

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
    }

    /*
     * Creates tiles for the imagezoom plugin and does some image processing
     * depends on having graphicsmagick on the path
     */
    /*def zoomifyImage(String filePath) {
        Runtime rt = Runtime.getRuntime()

        //fomat path
        def formattedPath = makePathURLSafe(filePath)
        //def exactOriginalPath = FilenameUtils.separatorsToSystem(filePath)
        def exactFormattedPath = FilenameUtils.separatorsToSystem(formattedPath)

        //create a "safe filename" copy
        def tempFile = new File("${exactFormattedPath}.tif")
        FileUtils.copyFile(new File(filePath), tempFile)

        // on windows we need to use cmd /c before we can execute any program
        def graphicsMagickCommand
        if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
            graphicsMagickCommand = "cmd /c gm convert ${exactFormattedPath}.tif -recolor \"1 1 1, 0 0 0, 0 0 0\" -rotate \"-90<\" -normalize ${exactFormattedPath}.jpg"
        else graphicsMagickCommand = ["gm", "convert", exactFormattedPath + ".tif", "-recolor", "1 1 1, 0 0 0, 0 0 0", "-rotate", "-90<", "-normalize", exactFormattedPath+".jpg"] as String[]

        try{
        //String

        //put all color information into the red channel and rotate if height > width by -90 degrees.
        Process pr = rt.exec(graphicsMagickCommand)
        int result = pr.waitFor()

        log.info "graphicsMagick command exit with status " + result

        def convertSettings = [:]
        convertSettings.numCPUCores = -1 //use all cores
        convertSettings.imgLib = "im4java-gm" // use graphics magick (alternative to im = imagemagick)

        def imagezoomFolder = grailsApplication.config.rppa.imagezoom.directory

        //create tiles
        imageConvertService.createZoomifyImage(imagezoomFolder, exactFormattedPath + ".jpg", convertSettings)

        //clean up
        new File(formattedPath+".jpg").delete()
        tempFile.delete()

        } catch(FileNotFoundException e)
        {
            log.error "There was an error during image processing. A file was not found:" + e.getMessage()
            println e.stackTrace
        }
    }    */

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

            if(resultFile && !resultFile.empty) objectInstance.resultFile = createResultFile(resultFile, "Result")

            if(resultImage && !resultImage.empty) objectInstance.resultImage = createResultFile(resultImage, "Image")

            if(protocol && !protocol.empty) objectInstance.protocol = createResultFile(protocol, "Protocol")
        }

        return objectInstance
    }
}
