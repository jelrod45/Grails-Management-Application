import rapco.*

class BootStrap {

    def init = { servletContext ->
    	def adminRole = new Role(authority: 'ROLE_ADMIN').save(flush: true)
    	def userRole = new Role(authority: 'ROLE_USER').save(flush: true)
	def admin = new User(username: 'admin', enabled: true, password: 'pass')
	admin.save(flush: true);

	UserRole.create(admin, adminRole, true);

	def mem = new Member(firstName: "Jay", middleName: "Chapman", lastName: "Elrod", address: "105 Mandy Dr Athens, GA 30601", phone: "4049662088");
	mem.save(flush: true);

	def comm = new Committee(name: "Board of Education", description: "Council that makes important decisions regarding educational institutions.", bylaws: "A whole bunch of laws...");
	comm.save(flush: true);

	new CommitteeMember(member: mem, committee: comm, title: "Chairman", startDate: new Date()-20, endDate: new Date()+4).save(flush: true);

    }
    def destroy = {
    }
}
