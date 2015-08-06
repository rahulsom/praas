package praas

import com.github.rahulsom.praas.Address
import com.github.rahulsom.praas.Identifier
import com.github.rahulsom.praas.Person
import com.github.rahulsom.praas.Provider
import com.github.rahulsom.praas.Taxonomy
import org.h2.tools.Csv
import org.hibernate.StatelessSession
import org.hibernate.Transaction

import java.sql.ResultSet
import java.text.SimpleDateFormat

class ProviderService {

    def sessionFactory
    def elasticSearchService

    def loadData(String fileName) {
        StatelessSession session = sessionFactory.openStatelessSession()
        Transaction tx = session.beginTransaction()

        Provider.executeUpdate('DELETE from Provider')
        def rs = new Csv().read(new FileReader(fileName), null)
        def fieldNameMap = [
                "NPI"                                             : 'npi',
                "Entity Type Code"                                : 'entityTypeCode',
                "Replacement NPI"                                 : 'replacementNpi',
                "Employer Identification Number (EIN)"            : 'ein',
                "Last Update Date"                                : 'lastUpdateDate',
                "NPI Deactivation Reason Code"                    : 'npiDeactivationReasonCode',
                "NPI Deactivation Date"                           : 'npiDeactivationDate',
                "NPI Reactivation Date"                           : 'npiReactivationDate',
                "Is Sole Proprietor"                              : 'isSoleProprietor',
                "Is Organization Subpart"                         : 'isOrganizationSubpart',
                "Parent Organization LBN"                         : 'parentOrganizationLbn',
                "Parent Organization TIN"                         : 'parentOrganizationTin',
                "Provider Organization Name (Legal Business Name)": 'organizationName',
                "Authorized Official Title or Position"           : 'authorizedOfficialTitle',
                "Authorized Official Telephone Number"            : 'authorizedOfficialPhone',
                "Provider Other Last Name Type Code"              : 'otherLastNameTypeCode',
                "Provider Other Organization Name"                : 'otherOrganizationName',
                "Provider Other Organization Name Type Code"      : 'otherOrganizationTypeCode',
        ]
        def personMap = [
                '$ Last Name'             : 'lastName',
                '$ Last Name (Legal Name)': 'lastName',
                '$ First Name'            : 'firstName',
                '$ Middle Name'           : 'middleName',
                '$ Name Prefix Text'      : 'prefix',
                '$ Name Suffix Text'      : 'suffix',
                '$ Credential Text'       : 'credential',
        ]
        def addressMap = [
                'Provider First Line $'                    : 'firstLine',
                'Provider Second Line $'                   : 'secondLine',
                'Provider $ City Name'                     : 'city',
                'Provider $ State Name'                    : 'state',
                'Provider $ Postal Code'                   : 'postalCode',
                'Provider $ Country Code (If outside U.S.)': 'countryCode',
                'Provider $ Telephone Number'              : 'phone',
                'Provider $ Fax Number'                    : 'fax',
        ]

        def identifierMap = [
                'Other Provider Identifier_$'          : 'identifier',
                'Other Provider Identifier Type Code_$': 'type',
                'Other Provider Identifier State_$'    : 'state',
                'Other Provider Identifier Issuer_$'   : 'issuer'
        ]

        def taxonomyMap = [
                'Healthcare Provider Taxonomy Code_$'          : 'codeName',
                'Provider License Number_$'                    : 'licenseNumber',
                'Provider License Number State Code_$'         : 'state',
                'Healthcare Provider Primary Taxonomy Switch_$': 'switchCode'
        ]

        int batchSize = 0
        long lastCheck = System.nanoTime()
        while (rs.next()) {
            def provider = fillDomainClass(Provider, rs, fieldNameMap)

            provider.authorizedOfficial = fillDomainClass(Person, rs, personMap, 'Authorized Official')
            provider.other = fillDomainClass(Person, rs, personMap, 'Provider Other')
            provider.provider = fillDomainClass(Person, rs, personMap, 'Provider')
            provider.mailingAddress = fillDomainClass(Address, rs, addressMap, 'Business Mailing Address')
            provider.practiceLocation = fillDomainClass(Address, rs, addressMap, 'Business Practice Location Address')

            provider.save()

            for (int i = 1; i < 38; i ++) {
                def identifier = fillDomainClass(Identifier, rs, identifierMap, i.toString())
                identifier.provider = provider
                if (identifier.validate()) {
                    identifier.save()
                }
            }
            for (int i = 1; i < 16; i ++) {
                def taxonomy = fillDomainClass(Taxonomy, rs, taxonomyMap, i.toString())
                taxonomy.provider = provider
                if (taxonomy.validate()) {
                    taxonomy.save()
                }
            }

            provider.save(flush: ++batchSize % 200 == 0)

            if (++batchSize % 200 == 0) {
                long newCheck = System.nanoTime()
                println "${batchSize} providers down in ${(newCheck - lastCheck) / 1000000.0} ms"
                lastCheck = newCheck
            }
        }

        tx.commit()
        session.close()

        elasticSearchService.unindex(Provider)
        elasticSearchService.index(Provider)

    }

    private static <T> T fillDomainClass(
            Class<T> clazz, ResultSet rs, LinkedHashMap<String, String> fieldNameMap, String prefix = null
    ) {
        def object = clazz.newInstance()
        fieldNameMap.each { String k, String v ->
            try {

                if (prefix) {
                    k = k.replace('$', prefix)
                }
                if (object.metaClass.properties.find { it.name == v }.type.isAssignableFrom(String)) {
                    object[v] = rs.getString(k)
                } else if (object.metaClass.properties.find { it.name == v }.type.isAssignableFrom(Integer)) {
                    object[v] = rs.getInt(k)
                } else if (object.metaClass.properties.find { it.name == v }.type.isAssignableFrom(Date)) {
                    def strVal = rs.getString(k)
                    if (strVal) {
                        object[v] = new SimpleDateFormat('MM/dd/yyyy').parse(strVal)
                    }
                } else {
                    println "Unhandled field type: ${object.metaClass.properties[v].class}"
                }
            } catch (Exception ignore) {
            }
        }

        object
    }
}
