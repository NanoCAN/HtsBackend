package org.nanocan.layout

class Sample implements Serializable{

    String name
    String type
    String target
    String color
    boolean control
    String controlType
    String uuid
    String sampleGroupA
    String sampleGroupADescription
    String sampleGroupB
    String sampleGroupBDescription
    String sampleGroupC
    String sampleGroupCDescription

    static hasMany = [identifiers: Identifier]

    static constraints = {
        uuid nullable: true
        sampleGroupA nullable: true
        sampleGroupADescription nullable: true
        sampleGroupB nullable: true
        sampleGroupBDescription nullable: true
        sampleGroupC nullable: true
        sampleGroupCDescription nullable: true
        name unique: true
        controlType inList: ["positive", "negative", "transfection", "kill"], nullable: true
        type inList: ["siRNA", "miRNA inhibitor", "miRNA mimic", "compound", "unknown", "cell-line", "tissue"]
        target nullable: true
        color unique:  false, validator:  {val, obj -> (val != "#ffffff" && val != "#e0e0e0")}, nullable: false
    }

    String toString()
    {
        name
    }
}
