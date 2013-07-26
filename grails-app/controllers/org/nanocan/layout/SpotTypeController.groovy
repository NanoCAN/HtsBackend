package org.nanocan.layout

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_USER'])
class SpotTypeController {

    def scaffold = true

}
