package org.nanocan.layout

class Identifier implements Serializable{

    String name
    String accessionNumber
    String type

    static belongsTo = [sample: Sample]

    static constraints = {
        name()
        type inList: ["miRBase", "NCBI"]
    }
}
