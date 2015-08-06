package com.github.rahulsom.praas

/**
 * Created by rsom on 9/22/14.
 */
class Taxonomy {

    static searchable = true

    String switchCode
    String codeName
    String licenseNumber
    String state

    static constraints = {
        codeName blank: false
    }

    static belongsTo = [
            provider: Provider
    ]

    @Override
    public String toString() {
        "(${switchCode}) - ${codeName} - ${licenseNumber}@${state}"
    }
}
