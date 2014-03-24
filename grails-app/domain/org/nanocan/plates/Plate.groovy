package org.nanocan.plates

import org.codehaus.jackson.annotate.JsonIgnore
import org.nanocan.layout.PlateLayout
import org.nanocan.project.Experiment

class Plate implements Serializable{

    PlateType plateType
    String format
    String barcode
    String name
    PlateLayout plateLayout
    Experiment experiment
    String uuid

    @JsonIgnore
    boolean controlPlate

    static belongsTo = [plateLayout: PlateLayout, experiment: Experiment]

    int replicate

    int cols
    int rows

    static mapping = {

    }

    static hasMany = [readouts: Readout]

    static constraints = {
        uuid nullable: true
        //barcodes need to be unique across the database
        barcode unique:  true
        name nullable: true, blank: false, unique: true
        plateType nullable: true
        format inList: ["96-well", "384-well"], blank: false, editable: false
        //we can only have one replicate x for each library plate y in experiment z
        replicate(unique: ['experiment', 'plateLayout'])
        rows editable: false
        cols editable: false
        experiment editable: false
    }

    String toString(){
      ("Plate" + barcode?:"no barcode" + name?:"")
    }

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
}
