package org.nanocan.layout

import org.springframework.security.access.annotation.Secured

/**
 * Created by mlist on 3/24/14.
 */
@Secured(['ROLE_USER'])
class ExtractionHeadController {

    def scaffold = true
}
