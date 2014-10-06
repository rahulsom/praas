package com.github.rahulsom.praas

import grails.validation.Validateable

/**
 * Created by rsom on 9/22/14.
 */
@Validateable
class Person {
    String lastName
    String firstName
    String middleName
    String prefix
    String suffix
    String credential

    static constraints = {
        credential nullable: true
    }
}
