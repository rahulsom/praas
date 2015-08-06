package com.github.rahulsom.praas

import grails.validation.Validateable

/**
 * Created by rsom on 9/22/14.
 */
class Person {

    static searchable = true

    String lastName
    String firstName
    String middleName
    String prefix
    String suffix
    String credential

    static constraints = {
        middleName nullable: true
        prefix nullable: true
        suffix nullable: true
        credential nullable: true
    }

    @Override
    String toString() {
        [prefix, firstName, middleName, lastName, suffix, credential].findAll{it}.join(' ')
    }
}
