package org.nanocan.io

import com.github.kevinsawicki.http.*
import groovy.json.JsonSlurper

class OpenLabFrameworkService {

    def grailsApplication

    def searchCellLineData(String query) {

        def openlabframeworkUrl = grailsApplication.config.openlabframework.rest.url
        def token = grailsApplication.config.openlabframework.appAccessToken

        if(!openlabframeworkUrl || !token){
            return(null)
        }
        String queryUrl = openlabframeworkUrl + "restful/search?type=cellLineData&query=" + java.net.URLEncoder.encode(query, "UTF-8") + "*" + "&token=" + token + "&max=15"
        def req = HttpRequest.get(queryUrl)

        req.trustAllCerts()
        req.trustAllHosts()

        def jsonResult = new JsonSlurper().parse(req.bufferedReader())

        if(!jsonResult.results) return []
        else return(jsonResult.results)
    }

    def redirectToCellLineData(String id){
        def openlabframeworkUrl = grailsApplication.config.openlabframework.rest.callback.url
        if(!openlabframeworkUrl) openlabframeworkUrl = grailsApplication.config.openlabframework.rest.url
        if(!openlabframeworkUrl) return(null)

        String queryUrl = openlabframeworkUrl + "#bodyContent=" + "/OpenLabFramework/cellLineData/show/%3Fid%3D" + id
        return(queryUrl)
    }
}
