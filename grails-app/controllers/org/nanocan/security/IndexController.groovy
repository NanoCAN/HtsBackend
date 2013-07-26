package org.nanocan.security

import grails.plugins.springsecurity.Secured

@Secured(['IS_AUTHENTICATED_FULLY'])

class IndexController {

    def index() { }
}
