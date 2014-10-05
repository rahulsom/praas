import com.codahale.metrics.health.HealthCheck
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck
import com.codahale.metrics.jvm.FileDescriptorRatioGauge
import com.codahale.metrics.jvm.GarbageCollectorMetricSet
import com.codahale.metrics.jvm.MemoryUsageGaugeSet
import com.codahale.metrics.jvm.ThreadStatesGaugeSet
import com.github.rahulsom.praas.Provider
import com.github.rahulsom.praas.auth.*
import org.grails.plugins.metrics.groovy.HealthChecks
import org.grails.plugins.metrics.groovy.Metrics

class BootStrap {

  def init = { servletContext ->

    User.withNewTransaction {
      if (!Role.count) {
        def adminRole = new Role(authority: 'ROLE_ADMIN').save(flush: true)
        def userRole = new Role(authority: 'ROLE_USER').save(flush: true)

        def admin = new User(username: 'admin', password: 'admin').save(flush: true)
        def user = new User(username: 'user', password: 'user').save(flush: true)
        def rahul = new User(username: 'rahul', password: 'rahul').save(flush: true)

        UserRole.create admin, adminRole, true
        UserRole.create user, userRole, true
        UserRole.create rahul, adminRole, true
        UserRole.create rahul, userRole, true
      }
    }

    HealthChecks.register('users',new HealthCheck() {
      @Override
      protected HealthCheck.Result check() throws Exception {
        return User.count() ?
            HealthCheck. Result.healthy("${User.count()} users found") :
            HealthCheck.Result.unhealthy('No users found')
      }
    })

    HealthChecks.register('provider',new HealthCheck() {
      @Override
      protected HealthCheck.Result check() throws Exception {
        return Provider.count() ?
            HealthCheck. Result.healthy("${Provider.count()} providers found") :
            HealthCheck.Result.unhealthy('No providers found')
      }
    })

    Metrics.newGauge('fileDescriptorRatio', new FileDescriptorRatioGauge())
    Metrics.getOrAdd('gcMetricSet',new GarbageCollectorMetricSet())
    Metrics.getOrAdd('memoryUsage',new MemoryUsageGaugeSet())
    Metrics.getOrAdd('threadStates',new ThreadStatesGaugeSet())

    HealthChecks.register('threadDeadlock', new ThreadDeadlockHealthCheck())

  }

  def destroy = {
  }
}
