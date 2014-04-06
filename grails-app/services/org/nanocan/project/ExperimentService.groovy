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
package org.nanocan.project

import org.nanocan.layout.SlideLayout
import org.nanocan.layout.PlateLayout

class ExperimentService {

    def findExperiment(doInstance) {
        return Experiment.where {
            if(doInstance instanceof SlideLayout) {
                slideLayouts
                {
                    id == doInstance.id
                }
            }
            /*else if(doInstance instanceof Slide)
            {
                slides
                {
                    id == doInstance.id
                }
            } */
            else if(doInstance instanceof PlateLayout)
            {
                plateLayouts
                {
                    id == doInstance.id
                }
            }
        }.list()

    }

    def addToExperiment(doInstance, experimentsSelected) {
        experimentsSelected.each {
            def experiment
            if(it instanceof Experiment) experiment = it
            else experiment = Experiment.get(it as Long)

            if(doInstance instanceof SlideLayout) experiment.addToSlideLayouts(doInstance).save(flush: true)
            //else if(doInstance instanceof Slide) experiment.addToSlides(doInstance).save(flush: true)
            else if(doInstance instanceof PlateLayout) experiment.addToPlateLayouts(doInstance).save(flush:true)
        }
    }

    def removeFromExperiment(doInstance, experimentsToRemoveFrom)
    {
        experimentsToRemoveFrom.each {
            def experiment
            if(it instanceof Experiment) experiment = it
            else experiment = Experiment.get(it as Long)

            if(doInstance instanceof SlideLayout) experiment.removeFromSlideLayouts(doInstance).save(flush: true)
            //else if(doInstance instanceof Slide) experiment.removeFromSlides(doInstance).save(flush: true)
            else if(doInstance instanceof PlateLayout) experiment.removeFromPlateLayouts(doInstance).save(flush: true)
        }
    }

    def updateExperiments(doInstance, experimentsSelected) {

        def experiments = findExperiment(doInstance)

        if(experimentsSelected == null && experiments == null) return

        else if(experimentsSelected == null && experiments.size() > 0)
        {
            removeFromExperiment(doInstance, experiments)
        }

        else if(experimentsSelected != null && experiments.size() == 0)
        {
            addToExperiment(doInstance, experimentsSelected)
        }

        else{
            experimentsSelected = experimentsSelected.collect{Experiment.get(it as Long)}

            def experimentsToRemoveFrom = experiments
            experimentsToRemoveFrom.removeAll(experimentsSelected)

            def experimentsToAddTo = experimentsSelected
            experimentsToAddTo.removeAll(experiments)

            if(experimentsToAddTo.size() > 0) addToExperiment(doInstance, experimentsToAddTo)
            if(experimentsToRemoveFrom.size() >0) removeFromExperiment(doInstance, experimentsToRemoveFrom)
        }
        return
    }
}
