package com.github.rahulsom.praas

import grails.validation.Validateable

/**
 * Created by rsom on 9/22/14.
 */
@Validateable
class Address {
    String city
    String countryCode
    String fax
    String postalCode
    String state
    String phone
    String secondLine
}
