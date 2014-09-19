import com.github.rahulsom.praas.auth.*

class BootStrap {

  def init = { servletContext ->
    User.withNewTransaction {
      if (!Role.count) {
        def adminRole = new Role(authority: 'ROLE_ADMIN').save(flush: true)
        def userRole = new Role(authority: 'ROLE_USER').save(flush: true)

        def admin = new User(username: 'admin', password: 'admin').save(flush: true)
        def user = new User(username: 'user', password: 'user').save(flush: true)

        UserRole.create admin, adminRole, true
        UserRole.create user, userRole, true
      }
    }

  }

  def destroy = {
  }
}
