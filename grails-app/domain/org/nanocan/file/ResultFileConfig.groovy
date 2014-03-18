package org.nanocan.file

class ResultFileConfig {

    String name

    String mainColCol
    String mainRowCol
    String blockCol
    String rowCol
    String columnCol
    String fgCol
    String bgCol
    String flagCol
    String xCol
    String yCol
    String diameterCol

    int skipLines


    static constraints = {
        mainColCol nullable: true
        mainRowCol nullable: true
        unique: "name"
        blockCol nullable:true, validator: { val, obj ->
            if(val && (obj.mainColCol || obj.mainRowCol)) ['cannotUseBlockColAndMainRowMainColAtTheSameTime']
        }
    }

    String toString()
    {
        name
    }
}
