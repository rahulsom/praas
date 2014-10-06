package com.github.rahulsom.praas

import com.github.rahulsom.swaggydoc.SwaggyList
import com.github.rahulsom.swaggydoc.SwaggyShow
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiResponse
import com.wordnik.swagger.annotations.ApiResponses
import grails.converters.JSON
import grails.converters.XML
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import org.grails.plugins.metrics.groovy.Timed
import org.h2.tools.Csv
import org.hibernate.StatelessSession
import org.hibernate.Transaction

import java.sql.ResultSet
import java.text.SimpleDateFormat

import static org.springframework.http.HttpStatus.NOT_FOUND

@Transactional(readOnly = true)
@Secured('ROLE_USER')
@Api(value = "Provider", description = "Data on Providers",
        produces = 'application/json,application/hal+json,application/xml,text/html',
        consumes = 'application/json,application/xml,application/x-www-form-urlencoded'
)
class ProviderController {

    def elasticSearchService
    def sessionFactory
    static allowedMethods = [save: "POST", ]

    @SwaggyList
    @Timed(name='providersearch')
    def index() {
        params.max = Math.min(params.max ?: 10, 100)
        if (params.q) {
            def search = Provider.search(params.q, params)
            respond search.searchResults, model: [providerInstanceCount: search.total]
        } else {
            respond Provider.list(params), model: [providerInstanceCount: Provider.count()]
        }
    }

    @SwaggyShow
    @Timed(name='providershow')
    def show() {
        respond Provider.get(params.id)
    }

    @Secured('ROLE_ADMIN')
    @ApiOperation(value = "Save LOINC Codes", response = Void)
    @ApiResponses([
            @ApiResponse(code = 422, message = 'Bad Entity Received'),
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(name = 'file', paramType = 'form', required = true, dataType = 'string',
                    value="CSV File from downloaded zip. E.g. '/Users/rsom/Downloads/npidata_small.csv'"),
    ])
    def save() {
        StatelessSession session = sessionFactory.openStatelessSession()
        Transaction tx = session.beginTransaction()

        Provider.executeUpdate('DELETE from Provider')

        def rs = new Csv().read(new FileReader(params.file), null)
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
                "Provider Other Organization Name Type Code"      : 'otherOrganizationTypeCode'

/*

                Person authorizedOfficial
                Person provider
                Person other

                Address mailingAddress
                Address practiceLocation*/
        ]
        int batchSize = 0
        long lastCheck = System.nanoTime()
        while (rs.next()) {
            def provider = fillDomainClass(Provider, rs, fieldNameMap)
            // provider.save(flush: ++batchSize % 200 == 0)
            session.insert(provider)
            if (++batchSize %200 == 0) {
                long newCheck = System.nanoTime()
                println "${batchSize} providers down in ${(newCheck - lastCheck)/1000000.0} ms"
                lastCheck = newCheck
            }
        }

        tx.commit()
        session.close()

        elasticSearchService.unindex(Provider)
        elasticSearchService.index(Provider)

        withFormat {
            html {
                render("Complete!")
            }
            json {
                def retval = [status: 'Complete']
                render retval as JSON
            }
            xml {
                def retval = [status: 'Complete']
                render retval as XML
            }
        }
    }

    private static <T> T fillDomainClass(
            Class<T> clazz, ResultSet rs, LinkedHashMap<String, String> fieldNameMap, String prefix = null
    ) {
        def object = clazz.newInstance()
        fieldNameMap.each { String k, String v ->
            if (prefix) {
                k = prefix + k
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
        }
        object
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'provider.label', default: 'Provider'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
