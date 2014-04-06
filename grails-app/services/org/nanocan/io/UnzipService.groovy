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

import org.apache.commons.io.IOUtils

import java.util.zip.ZipFile

class UnzipService {

    def HashMap<String, File> Unpack(InputStream iStream) {


        def zipFile = new ZipFile(getFileFromStream(iStream, ".zip"))

        def unpackedList = new HashMap<String, File>()

        // Do stuff for each entry
        zipFile.entries().each {
            innerFile ->

                // Only files
                if(innerFile.isDirectory()){
                    return
                }

                if(!innerFile.name.contains(".")){
                    return
                }

                String suffixWithDot = innerFile.name.substring(innerFile.name.lastIndexOf("."))

                unpackedList.put(
                        innerFile.name,
                        getFileFromStream(
                                zipFile.getInputStream(innerFile),
                                suffixWithDot
                        )
                )

        }

        return unpackedList
    }

    def getFileFromStream(InputStream inputStream, String suffixWithDot){

        // Creating a temp file
        File tempFile = File.createTempFile("newTempFile", suffixWithDot)

        // Deletes the file once we're done using it
        tempFile.deleteOnExit()

        // Copy the zip-file-stream -> temp file -> ZipFile
        FileOutputStream out = new FileOutputStream(tempFile)
        IOUtils.copy(inputStream, out);

        return tempFile
    }
}