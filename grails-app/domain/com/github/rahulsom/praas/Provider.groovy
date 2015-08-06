package com.github.rahulsom.praas

class Provider {

    static searchable = {

        authorizedOfficial component: true
        provider component: true
        other component: true

        mailingAddress component: true
        practiceLocation component: true

        identifiers reference: true
        licenses reference: true
        taxonomies reference: true

    }

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

    static constraints = {
        lastUpdateDate nullable: true
        npiReactivationDate nullable: true
        npiDeactivationDate nullable: true

        provider nullable: true
        other nullable: true
        authorizedOfficial nullable: true

        mailingAddress nullable: true
        practiceLocation nullable: true
    }

    static mapping = {
        provider cascade: 'all'
        other cascade: 'all'
        authorizedOfficial cascade: 'all'

        mailingAddress cascade: 'all'
        practiceLocation cascade: 'all'
    }
}
