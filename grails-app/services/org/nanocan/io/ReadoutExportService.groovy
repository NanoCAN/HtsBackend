package org.nanocan.io

import org.hibernate.criterion.CriteriaSpecification
import org.nanocan.layout.WellLayout
import org.nanocan.plates.WellReadout

class ReadoutExportService {

    def getWellReadouts(readoutId){
        def criteria = WellReadout.createCriteria()
        def result = criteria.list {
            eq("readout.id", readoutId)
            createAlias('readout', 'ro', CriteriaSpecification.LEFT_JOIN)
            createAlias('ro.plate', 'pl', CriteriaSpecification.LEFT_JOIN)
            createAlias('ro.assay', 'assay', CriteriaSpecification.LEFT_JOIN)
            createAlias('pl.plateLayout', 'playout', CriteriaSpecification.LEFT_JOIN)
            projections {
                property "id"
                property "pl.id"
                property "ro.dateOfReadout"
                property "row"
                property "col"
                property "pl.replicate"
                property "playout.id"
                property "measuredValue"
                property "assay.name"
                property "assay.type"
            }
            order('row', 'desc')
            order('col', 'asc')
        }

        return(result)
    }

    def getPlateLayout(plateLayoutId){
        def criteria = WellLayout.createCriteria()
        def result = criteria.list {
            eq("plateLayout.id", plateLayoutId)
            createAlias('plateLayout', 'pl', CriteriaSpecification.LEFT_JOIN)
            createAlias('sample', 'smpl', CriteriaSpecification.LEFT_JOIN)
            createAlias('smpl.identifiers', 'ident', CriteriaSpecification.LEFT_JOIN)
            createAlias('numberOfCellsSeeded', 'ncs', CriteriaSpecification.LEFT_JOIN)
            createAlias('cellLine', 'cl', CriteriaSpecification.LEFT_JOIN)
            createAlias('treatment', 'tr', CriteriaSpecification.LEFT_JOIN)
            createAlias('inducer', 'ind', CriteriaSpecification.LEFT_JOIN)
            projections {
                property "id"
                property "pl.id"
                property "row"
                property "col"
                property "ncs.name"
                property "cl.name"
                property "ind.name"
                property "ind.concentration"
                property "tr.name"
                property "smpl.name"
                property "smpl.controlType"
                property "ident.accessionNumber"
                property "ident.type"
            }
            order('row', 'desc')
            order('col', 'asc')
        }

        return(result)
    }
}
