package org.nanocan.file

import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_USER'])
class ResultFileConfigController {

    def scaffold = true
}
