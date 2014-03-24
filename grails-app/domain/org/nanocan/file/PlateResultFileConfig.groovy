package org.nanocan.file

class PlateResultFileConfig {

    String name


    String rowCol
    String columnCol
    int measuredValue

    int skipLines


    static constraints = {

        unique: "name"
    }

    String toString()
    {
        name
    }
}
