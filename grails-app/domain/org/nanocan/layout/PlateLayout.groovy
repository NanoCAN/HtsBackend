package org.nanocan.layout

import org.codehaus.jackson.annotate.JsonIgnore
import org.nanocan.plates.Plate
import org.nanocan.security.Person

class PlateLayout implements Serializable{

    String name
    String format

    int cols
    int rows

    Date dateCreated
    Date lastUpdated

    Person createdBy
    Person lastUpdatedBy

    static constraints = {
        name blank: false, unique:  true, nullable: false
        format inList: ["96-well", "384-well"], blank: false, editable: false
    }

    static hasMany = [wells: WellLayout, plates: Plate]
    SortedSet wells

    def beforeInsert = {
        if(format == "96-well")
        {
            cols = 12
            rows = 8
        }
        else if(format == "384-well")
        {
            cols = 24
            rows = 16
        }
    }

    def beforeUpdate = {
        beforeInsert()
    }

    String toString()
    {
        name
    }
}
