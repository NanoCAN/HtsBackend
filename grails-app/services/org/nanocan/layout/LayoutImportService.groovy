package org.nanocan.layout

import groovy.sql.Sql
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile

class LayoutImportService {

    def grailsApplication
    def colorService
    def progressService
    def dataSourceUnproxied

    def importLayoutFromFile(def content, SlideLayout slideLayoutInstance) {

        //need to save slideLayoutInstance
        slideLayoutInstance.save(flush:true, failOnError: true)
        //header needs to look like
        // MIRACLE layout ...
        // version=1.0
        // title=test
        // Block \t Column \t Row \t ...

        int currentBlock = 0
        int currentRow = 0
        int currentCol = 0

        def scanner = new Scanner(content)

        //get config
        int batchSize = grailsApplication.config.rppa.jdbc.batchSize?:200
        boolean useGroovySql = false //grailsApplication.config.rppa.jdbc.groovySql.toString().toBoolean()

        log.debug "using groovy sql instead of GORM:" + useGroovySql

        //skip header
        for(int i = 1; i <= 4; i++){
            if(scanner.hasNextLine()) scanner.nextLine()
        }

        def currentLine = "empty"

        //read first line
        if(scanner.hasNextLine())
            currentLine = scanner.nextLine().split("\t")

        def insertLoop = { stmt ->
            for (int block = 1; block <= slideLayoutInstance.numberOfBlocks; block++) {
                for (int row = 1; row <= slideLayoutInstance.rowsPerBlock; row++) {
                    for (int col = 1; col <= slideLayoutInstance.columnsPerBlock; col++) {

                        try{
                            currentBlock = Integer.valueOf(currentLine[0])
                            currentRow = Integer.valueOf(currentLine[1])
                            currentCol = Integer.valueOf(currentLine[2])
                        } catch(Exception e){
                            log.warn "could not parse integer, skipping line"
                        }

                        if(currentBlock == block && currentRow == row && currentCol == col)
                        {
                            //add layout spot using line
                            def layoutSpot = createLayoutSpot(currentLine)
                            layoutSpot.layout = slideLayoutInstance
                            layoutSpot.block = block
                            layoutSpot.row = row
                            layoutSpot.col = col
                            if (useGroovySql) layoutSpot.save(flush:true, failOnError: true)  //TODO use groovysql
                            else layoutSpot.save(flush:true, failOnError: true)

                            if(scanner.hasNextLine()) currentLine = scanner.nextLine().split("\t")
                        }
                        //no layout information in file, create empty layout spot
                        else{
                            if (useGroovySql) stmt.addBatch(0, block, null, col, null, null, slideLayoutInstance.id, null, row, null, null, 1, null)
                            else new LayoutSpot(block: block, col: col, row: row, layout: slideLayoutInstance, replicate:1).save()
                        }
                    }
                }
            }
        }

        if(useGroovySql){
            //create an sql instance for direct inserts via groovy sql
            def sql = Sql.newInstance(dataSourceUnproxied)

            sql.withBatch(batchSize, 'insert into layout_spot (version, block, cell_line_id, col, dilution_factor_id, inducer_id, layout_id, lysis_buffer_id, row, sample_id, spot_type_id, replicate, well_layout_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)'){ stmt ->
                insertLoop(stmt)
            }
            //clean up
            sql.close()

            //refresh slide layout, because hibernate does not know about our changes
            slideLayout.refresh()
        }

        else insertLoop(null)
    }


    def createLayoutSpot = { it ->

        def lspot = new LayoutSpot()

        def sampleName = it[4]
        def uuid = it[6]

        def newSample = Sample.findByUuid(uuid)
        if(!newSample) newSample = Sample.findByName(sampleName)

        if(!newSample)
        {
            def sampleType = it[3]
            def controlType = it[5]

            def color = colorService.randomColor()
            while(Sample.findByColor(color)){
                color = colorService.randomColor()
            }

            if(controlType && controlType.length() > 0)
                newSample = new Sample(name: sampleName, type: sampleType, control: true, color:color, controlType: controlType)
            else
                newSample = new Sample(name: sampleName, type: sampleType, control: false, color: color)

            newSample.sampleGroupADescription = it[7]
            newSample.sampleGroupA = it[8]
            newSample.sampleGroupBDescription = it[9]
            newSample.sampleGroupB = it[10]
            newSample.sampleGroupCDescription = it[11]
            newSample.sampleGroupC = it[12]
            newSample.save(flush:true, failOnError: true)
        }

        lspot.sample = newSample

        def cellLine = it[13]

        if(cellLine && cellLine.length() > 0)
        {
            def cellLineInstance = CellLine.findByName(cellLine)
            if(!cellLineInstance){
                def cellLineColor =  colorService.randomColor()
                while(CellLine.findByColor(cellLineColor)){
                    cellLineColor = colorService.randomColor()
                }

                cellLineInstance = new CellLine(name: cellLine, color: cellLineColor)
                cellLineInstance.save(flush:true, failOnError: true)
            }
            lspot.cellLine = cellLineInstance
        }

        def numberOfCellsSeeded = it[14]

        if(numberOfCellsSeeded && numberOfCellsSeeded.length() > 0){
            def numberOfCellsSeededInstance = NumberOfCellsSeeded.findByName(numberOfCellsSeeded)
            if(!numberOfCellsSeededInstance){
                def numberOfCellsSeededColor =  colorService.randomColor()
                while(NumberOfCellsSeeded.findByColor(numberOfCellsSeededColor)){
                    numberOfCellsSeededColor = colorService.randomColor()
                }
                numberOfCellsSeededInstance = new NumberOfCellsSeeded(name: numberOfCellsSeeded, color: numberOfCellsSeededColor)
                numberOfCellsSeededInstance.save(flush:true, failOnError: true)
            }
            lspot.numberOfCellsSeeded = numberOfCellsSeededInstance
        }

        def inducer = it[15]

        if(inducer && inducer.length() > 0){
          def inducerInstance = Inducer.findByName(inducer)
          if(!inducerInstance){
              def inducerColor =  colorService.randomColor()
              while(Inducer.findByColor(inducerColor)){
                  inducerColor = colorService.randomColor()
              }
              inducerInstance = new Inducer(name: inducer, color: inducerColor)
              inducerInstance.save(flush:true)
          }

          lspot.inducer = inducerInstance
        }

        def treatment = it[16]

        if(treatment && treatment.length() > 0){
            def treatmentInstance = Treatment.findByName(treatment)
            if(!treatmentInstance){
                def treatmentColor =  colorService.randomColor()
                while(Treatment.findByColor(treatmentColor)){
                    treatmentColor = colorService.randomColor()
                }

                treatmentInstance = new Treatment(name: treatment, color: treatmentColor)
                treatmentInstance.save(flush:true, failOnError: true)
            }
            lspot.treatment = treatmentInstance
        }

        def spotType = it[17]
        def spotClass = it[18]

        if(spotType && spotType.length() > 0) {
            def spotTypeInstance = SpotType.findByName(spotType)
            if(!spotTypeInstance){
                def spotTypeColor =  colorService.randomColor()
                while(SpotType.findByColor(spotTypeColor)){
                    spotTypeColor = colorService.randomColor()
                }

                spotTypeInstance = new SpotType(name: spotType, color: spotTypeColor, type: spotClass)
                spotTypeInstance.save(flush: true, failOnError: true)
            }
            lspot.spotType = spotTypeInstance
        }

        return(lspot)
    }
}
