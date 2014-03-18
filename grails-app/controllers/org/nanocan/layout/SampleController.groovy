package org.nanocan.layout

import grails.plugins.springsecurity.Secured
import grails.converters.JSON

@Secured(['ROLE_USER'])
class SampleController {

    def scaffold = true

    def listControls(){

        def controls = Sample.findAllByControl(true)

        render(view: "list", model : [sampleInstanceList: controls, sampleInstanceTotal: controls.size() ])
    }

    def ajaxSampleFinder = {
        def samplesFound = Sample.withCriteria {
            and{
                isEmpty("identifiers")

                or
                {
                    ilike 'name', params.term + '%'
                    ilike 'target', params.term + '%'
                }
            }
        }

        def moreSamplesFound = Sample.withCriteria {
            or
            {
                ilike 'name', params.term + '%'
                ilike 'target', params.term + '%'
                identifiers{
                   or
                   {
                     ilike 'name', params.term + '%'
                     ilike 'accessionNumber', params.term + '%'
                   }
                }
            }
        }

        samplesFound.addAll(moreSamplesFound)

        def sampleSelectList = []

        samplesFound.each{
            def resultMap = [:]
            resultMap.put("id", it.id)
            resultMap.put("label", it.name)
            resultMap.put("value", it.name)
            resultMap.put("colour", it.color)
            sampleSelectList.add(resultMap)
        }

        render (sampleSelectList as JSON)
    }

    def legendSampleSelected = {

        render "<script>alert('test');<script>"
    }
}
