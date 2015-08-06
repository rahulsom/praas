package com.github.rahulsom.praas

import grails.validation.Validateable

/**
 * Created by rsom on 9/22/14.
 */
class Address {

    static searchable = true

    String city
    String countryCode
    String fax
    String postalCode
    String state
    String phone
    String secondLine
    String firstLine

    @Override
    String toString() {
        def ad = "${firstLine} ${secondLine}, ${city}, ${state} ${postalCode}, ${countryCode}".replaceAll(' +', ' ')
        def ph = phone ? "Phone: $phone" : null
        def fx = fax ? "Fax: $fax" : null
        [ad, ph, fx].findAll {it}.join('; ')
    }
}
