package org.nanocan.project

import org.nanocan.layout.SlideLayout
import org.nanocan.security.Person
import org.nanocan.layout.PlateLayout

class Project implements Serializable {

    String projectTitle
    String projectDescription

    Date dateCreated
    Date lastUpdated

    Person createdBy
    Person lastUpdatedBy

    static hasMany = [experiments: Experiment]

    static constraints = {

        projectTitle unique:true
    }

    String toString()
    {
        projectTitle
    }
}
