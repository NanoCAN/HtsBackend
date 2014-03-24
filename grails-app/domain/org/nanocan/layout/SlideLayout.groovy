package org.nanocan.layout

import org.nanocan.plates.Plate
import org.nanocan.security.Person

class SlideLayout implements Serializable{

    Date dateCreated
    Date lastUpdated

    Person createdBy
    Person lastUpdatedBy
    
    String title
    int columnsPerBlock
    int rowsPerBlock
    int numberOfBlocks
    Integer blocksPerRow
    String depositionPattern
    List sourcePlates
    ExtractionHead extractionHead

    static hasMany = [sampleSpots: LayoutSpot, sourcePlates: Plate]

    SortedSet sampleSpots

    static constraints = {
        extractionHead nullable:true
        sourcePlates nullable: true
        title unique: true, blank: false
        columnsPerBlock min:  1
        rowsPerBlock min:  1
        numberOfBlocks min:  1
        blocksPerRow nullable: true, min: 1
        depositionPattern validator: {
            if(!(it ==~ /\[([1-9]+,)*+[1-9]+\]/)) return ("default.deposition.pattern.mismatch")
        }
    }
    
    String toString(){
        title
    }
}
