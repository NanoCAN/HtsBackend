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
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

/**
 * This service handles additional operations regarding the slide layout
 */
class SlideLayoutService {

    //dependencies
    def progressService
    def dataSourceUnproxied
    def grailsApplication

    /**
     * Creates a full set of layout spots with respect to the number of columns, blocks and rows.
     * @param slideLayout
     * @return
     */
    def createSampleSpots(SlideLayout slideLayout) {

        //get config
        int batchSize = grailsApplication.config.rppa.jdbc.batchSize?:200
        boolean useGroovySql = grailsApplication.config.rppa.jdbc.groovySql.toString().toBoolean()

        log.debug "using groovy sql instead of GORM:" + useGroovySql

        def insertLoop = { stmt ->
            for (int block = 1; block <= slideLayout.numberOfBlocks; block++) {
                for (int col = 1; col <= slideLayout.columnsPerBlock; col++) {
                    for (int row = 1; row <= slideLayout.rowsPerBlock; row++) {
                        if (useGroovySql) stmt.addBatch(0, block, null, col, null, null, slideLayout.id, null, row, null, null, 1, null)
                        else new LayoutSpot(block: block, col: col, row: row, layout: slideLayout, replicate:1).save()
                    }
                }
            }
        }

        if(useGroovySql){
            //create an sql instance for direct inserts via groovy sql
            def sql = Sql.newInstance(dataSourceUnproxied)

            sql.withBatch(batchSize, 'insert into layout_spot (version, block, cell_line_id, col, dilution_factor_id, inducer_id, layout_id, lysis_buffer_id, row, sample_id, spot_type_id, replicate, well_layout_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)'){ stmt ->
                insertLoop(stmt)
            }
            //clean up
            sql.close()

            //refresh slide layout, because hibernate does not know about our changes
            slideLayout.refresh()
        }

        else insertLoop(null)

        return null
    }

    def copySlideLayout(slideLayoutInstance, newName)
    {
        def newSlideLayout = new SlideLayout()
        newSlideLayout.dateCreated = new Date()
        newSlideLayout.lastUpdated = new Date()

        newSlideLayout.title = newName
        newSlideLayout.columnsPerBlock = slideLayoutInstance.columnsPerBlock
        newSlideLayout.rowsPerBlock = slideLayoutInstance.rowsPerBlock
        newSlideLayout.numberOfBlocks = slideLayoutInstance.numberOfBlocks
        newSlideLayout.blocksPerRow = slideLayoutInstance.blocksPerRow
        newSlideLayout.depositionDirection = slideLayoutInstance.depositionDirection
        newSlideLayout.depositionPattern = slideLayoutInstance.depositionPattern
        newSlideLayout.extractionHead = slideLayoutInstance.extractionHead
        slideLayoutInstance.sourcePlates.each{
            newSlideLayout.addToSourcePlates(it)
        }

        slideLayoutInstance.sampleSpots.each{
            newSlideLayout.addToSampleSpots(copySpot(it))
        }
        return(newSlideLayout)
    }

    def copySpot(layoutSpotInstance)
    {
        def layoutSpotDefaultDomainClass = new DefaultGrailsDomainClass(LayoutSpot.class)
        def newLayoutSpotInstance = new LayoutSpot()
        layoutSpotDefaultDomainClass.persistentProperties.each{prop ->
            if(prop.name != "slideLayout")
                newLayoutSpotInstance."${prop.name}" = layoutSpotInstance."${prop.name}"
        }
        return newLayoutSpotInstance
    }

    /**
     * The user can change spot properties in the web interface. This is a html table where each cell corresponds
     * to one layout spot. This method infers which property has been changed and updates the respective spot property.
     * @param spotProp
     * @param slideLayout
     * @return
     */
    def updateSpotProperties(params, spotProp, slideLayout, className) {
        //to calculate percentage for progress bar
        def numberOfSpots = params.keySet().size()
        def currentSpot = 0

        params.each {key, value ->
            if (value != "") {
                def spot = LayoutSpot.get(key as Long)

                def classPrefix = "org.nanocan.layout."

                if (value as Long == -1) spot.properties[spotProp] = null
                else spot.properties[spotProp] = grailsApplication.getDomainClass(classPrefix + className.toString().capitalize()).clazz.get(value as Long)
                spot.save()
            }

            progressService.setProgressBarValue("update${slideLayout}", (currentSpot / numberOfSpots * 100))
            currentSpot++
        }
    }

    /**
     * Attempts to delete layout spots efficiently
     * @param slideLayoutId
     */
    def deleteLayoutSpots(slideLayoutId){
        //config
        def groovySql = grailsApplication?.config?.rppa?.jdbc?.groovySql?.toString()?.toBoolean()?:true

        if(groovySql)
        {
            def sql = Sql.newInstance(dataSourceUnproxied)
            sql.execute('delete from layout_spot where layout_id = ?', slideLayoutId)
            sql.close()
        }

        else{
            def layoutInstance = SlideLayout.get(slideLayoutId)
            layoutInstance.spots.clear()
        }
    }
}


