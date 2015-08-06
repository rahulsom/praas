package com.github.rahulsom.praas

/**
 * Created by rsom on 9/22/14.
 */
class Identifier {

    static searchable = true

    String identifier
    String state
    String issuer
    String type

    static constraints = {
        identifier blank: false
    }

    static belongsTo = [
            provider: Provider
    ]

    @Override
    public String toString() {
        "${identifier}@${state} issued by ${issuer} (${type})"
    }
}
