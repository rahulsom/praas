package com.github.rahulsom.praas

class Provider {

    // static searchable = true

    String npi
    String entityTypeCode
    String replacementNpi
    Date npiDeactivationDate
    String npiDeactivationReasonCode
    Date npiReactivationDate

    String isOrganizationSubpart
    String isSoleProprietor

    Date lastUpdateDate

    String parentOrganizationLbn
    String parentOrganizationTin

    String ein
    String organizationName

    Person authorizedOfficial
    String authorizedOfficialTitle
    String authorizedOfficialPhone

    Person provider
    Person other
    String otherLastNameTypeCode
    String otherOrganizationName
    String otherOrganizationTypeCode

    Address mailingAddress
    Address practiceLocation

    static hasMany = [
            identifiers: Identifier,
            licenses: License,
            taxonomies: Taxonomy
    ]

    static embedded = [
            'authorizedOfficial', 'other', 'provider',
            'mailingAddress', 'practiceLocation'
    ]

    static constraints = {

    }
}
