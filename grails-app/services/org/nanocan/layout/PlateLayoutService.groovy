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

import groovy.sql.Sql
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsClass

class PlateLayoutService {

    def grailsApplication
    def progressService
    def dataSourceUnproxied

    /**
     * Creates a full set of layout spots with respect to the number of columns, blocks and rows.
     * @param plateLayout
     * @return
     */
    def createWellLayouts(PlateLayout plateLayout) {

        //get config
        int batchSize = grailsApplication.config.jdbc.batchSize ?: 96
        boolean useGroovySql = grailsApplication.config.jdbc.groovySql.toString().toBoolean()

        log.debug "using groovy sql instead of GORM:" + useGroovySql

        def insertLoop = { stmt ->
            for (int col = 1; col <= plateLayout.cols; col++) {
                for (int row = 1; row <= plateLayout.rows; row++) {

                    if (useGroovySql) stmt.addBatch(0, col, plateLayout.id, row)
                    else new WellLayout(col: col, row: row, plateLayout: plateLayout, numberOfCellsSeeded: null, inducer: null, treatment: null, sample: null).save(flush: true, failOnError: true)
                }
            }
        }

        if (useGroovySql) {
            //create an sql instance for direct inserts via groovy sql
            def sql = Sql.newInstance(dataSourceUnproxied)

            sql.withBatch(batchSize, 'insert into well_layout (version, col, plate_layout_id, row) values (?, ?, ?, ?)') { stmt ->
                insertLoop(stmt)
            }
            //clean up
            sql.close()

            //refresh slide layout, because hibernate does not know about our changes
            plateLayout.refresh()
        } else insertLoop(null)

        return null
    }

    /**
     * The user can change well layout properties in the web interface. This is a html table where each cell corresponds
     * to one layout well. This method infers which property has been changed and updates the respective well property.
     * @param wellProp
     * @param plateLayout
     * @return
     */
    def updateWellProperties(params, wellProp, plateLayout) {
        def numberOfWells = params.keySet().size()
        def currentWell = 0

        params.each { key, value ->
            if (value != "") {
                def well = WellLayout.get(key as Long)

                def classPrefix = "org.nanocan.layout."

                if (value as Long == -1) well.properties[wellProp] = null
                else well.properties[wellProp] = grailsApplication.getDomainClass(classPrefix + wellProp.toString().capitalize()).clazz.get(value as Long)

                well.save()
            }

            progressService.setProgressBarValue("update${plateLayout}", (currentWell / numberOfWells * 100))
            currentWell++
        }
    }

    def copyPlateLayout(plateLayoutInstance, newName) {
        def newPlateLayout = new PlateLayout()
        newPlateLayout.dateCreated = new Date()
        newPlateLayout.lastUpdated = new Date()

        newPlateLayout.format = plateLayoutInstance.format
        newPlateLayout.name = newName

        plateLayoutInstance.wells.each{
            newPlateLayout.addToWells(copyWell(it))
        }
        return(newPlateLayout)
    }

    def copyWell(wellLayoutInstance)
    {
        def wellsDefaultDomainClass = new DefaultGrailsDomainClass(WellLayout.class)
        def newWellLayoutInstance = new WellLayout()
        wellsDefaultDomainClass.persistentProperties.each{prop ->
            if(prop.name != "plateLayout")
                newWellLayoutInstance."${prop.name}" = wellLayoutInstance."${prop.name}"
        }
        return newWellLayoutInstance
    }
 }