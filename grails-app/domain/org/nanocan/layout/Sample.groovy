package org.nanocan.layout

class Sample implements Serializable{

    String name
    String type
    String target
    String color
    boolean control
    String controlType

    static hasMany = [identifiers: Identifier]

    static constraints = {
        name unique: true
        controlType inList: ["positive", "negative", "transfection", "kill"], nullable: true
        type inList: ["siRNA", "miRNA inhibitor", "miRNA mimic", "compound", "unknown"]
        target()
        color unique:  false, validator:  {val, obj -> (val != "#ffffff" && val != "#e0e0e0")}, nullable: false
    }

    String toString()
    {
        name
    }
}
