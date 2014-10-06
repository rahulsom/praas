package com.github.rahulsom.resterrors

import grails.converters.JSON
import grails.converters.XML
import org.springframework.http.HttpStatus

import javax.servlet.http.HttpServletResponse

class RestErrorsController {

    def index() {
        log.debug("App Error RS: ${response.status}")
        response.withFormat {
            html {
                redirect(view: '/error.gsp')
            }
            json {
                render ([
                        status: response.status,
                        message: HttpStatus.valueOf(response.status).reasonPhrase
                ] as JSON)
            }
            xml {
                render ([
                        status: response.status,
                        message: HttpStatus.valueOf(response.status).reasonPhrase
                ] as XML)
            }
        }
    }

}
