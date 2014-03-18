package org.nanocan.layout

import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile

class LayoutImportService {

    def grailsApplication
    def colorService

    def importSamplesFromFile(def request, SlideLayout slideLayoutInstance) {

        def file = request.getFile("resultFileInput");
        def content = file.getFileItem().getString()

        //header needs to look like Block \t Column \t Row \t Property  \t type
        content.splitEachLine('\t') {  it ->

            def block
            def row
            def col

            try{
               block = Integer.valueOf(it[0])
               row = Integer.valueOf(it[1])
               col = Integer.valueOf(it[2])
            } catch(Exception e){
                println "could not parse integer, skipping line"
                return
            }
            println it

            def lspot = LayoutSpot.withCriteria(uniqueResult: true){
                eq('layout.id', slideLayoutInstance.id)
                eq('block', block)
                eq('row', row)
                eq('col', col)
            }
            println "lspot:"
            println lspot

            def sampleName = it[3]

            def newSample = Sample.findByName(sampleName)

            if(!newSample)
            {
                def sampleType = it[4]
                def controlType = it[5]

                def color = colorService.randomColor()
                while(Sample.findByColor(color)){
                    color = colorService.randomColor()
                }
                println color

                if(controlType && controlType.length() > 0)
                    newSample = new Sample(name: sampleName, type: sampleType, control: true, color:color, controlType: controlType)
                else
                    newSample = new Sample(name: sampleName, type: sampleType, control: false, color: color)
            }
            println newSample.save(flush: true, failOnError: true)

            lspot.sample = newSample
            lspot.save(flush:true, failOnError: true)

        }

    }
}
