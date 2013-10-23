package org.nanocan.project

import org.nanocan.security.Person
import org.nanocan.layout.SlideLayout
import org.nanocan.layout.PlateLayout

class Experiment implements Serializable{

    String title
    String description

    Date dateCreated
    Date lastUpdated
    Date firstDayOfTheExperiment

    Person createdBy
    Person lastUpdatedBy

    static hasMany = [slideLayouts: SlideLayout, plateLayouts: PlateLayout]
    static belongsTo = [project: Project]

    static constraints = {

        title unique:true
        firstDayOfTheExperiment nullable: true
    }

    String toString()
    {
        /*if(firstDayOfTheExperiment)
            return ("${firstDayOfTheExperiment.toString()} - ${title}")
        else */return title
    }
}
