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

class ProjectService {

    def findProject(doInstance) {
        return Project.where {
            if(doInstance instanceof SlideLayout) {
                layouts
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
            }   */
        }.list()

    }

    def addToProject(doInstance, projectsSelected) {
        projectsSelected.each {
            def project
            if(it instanceof Project) project = it
            else project = Project.get(it as Long)

            if(doInstance instanceof SlideLayout) project.addToLayouts(doInstance).save(flush: true)
            //else if(doInstance instanceof Slide) project.addToSlides(doInstance).save(flush: true)
        }
    }

    def removeFromProject(doInstance, projectsToRemoveFrom)
    {
        projectsToRemoveFrom.each {
            def project
            if(it instanceof Project) project = it
            else project = Project.get(it as Long)

            if(doInstance instanceof SlideLayout) project.removeFromLayouts(doInstance).save(flush: true)
            //else if(doInstance instanceof Slide) project.removeFromSlides(doInstance).save(flush: true)
        }
    }

    def updateProjects(doInstance, projectsSelected) {

        def projects = findProject(doInstance)

        if(projectsSelected == null && projects == null) return

        else if(projectsSelected == null && projects.size() > 0)
        {
            removeFromProject(doInstance, projects)
        }

        else if(projectsSelected != null && projects.size() == 0)
        {
            addToProject(doInstance, projectsSelected)
        }

        else{
            projectsSelected = projectsSelected.collect{Project.get(it as Long)}

            def projectsToRemoveFrom = projects
            projectsToRemoveFrom.removeAll(projectsSelected)

            def projectsToAddTo = projectsSelected
            projectsToAddTo.removeAll(projects)

            if(projectsToAddTo.size() > 0) addToProject(doInstance, projectsToAddTo)
            if(projectsToRemoveFrom.size() >0) removeFromProject(doInstance, projectsToRemoveFrom)
        }
        return
    }
}
