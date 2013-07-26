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

    static hasMany = [layouts: SlideLayout, plateLayouts: PlateLayout]

    static constraints = {

        projectTitle unique:true
    }

    String toString()
    {
        projectTitle
    }
}
