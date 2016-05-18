package org.nanocan.plates

import grails.plugins.springsecurity.Secured


@Secured(['ROLE_USER'])
class AssayTypeController {

    def scaffold = true
}
