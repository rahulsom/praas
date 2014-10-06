class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "400"(controller: 'restErrors') // bad request
        "401"(controller: 'restErrors') // unauthorized
        "403"(controller: 'restErrors') // forbidden
        "405"(controller: 'restErrors') // method not allowed
        "406"(controller: 'restErrors') // not acceptable
        "408"(controller: 'restErrors') // request time out
        "409"(controller: 'restErrors') // conflict

        "500"(controller: 'restErrors') // internal server error
        "501"(controller: 'restErrors') // not implemented
        "503"(controller: 'restErrors') // service unavailable

	}
}
