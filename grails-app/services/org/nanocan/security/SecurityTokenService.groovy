package org.nanocan.security

class SecurityTokenService {

    def getSecurityToken(def secureObject) {
        if(!secureObject?.uuid){
            secureObject.uuid = UUID.randomUUID().toString()
            secureObject.save(flush:true)
        }

        return secureObject.uuid
    }
}
