package rapco

import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;

class DBMService {

    transient springSecurityService;
    static transactional = true;

    @Transactional
    def saveCommittee(name, desc, bylaws) {
    	if (Committee.findByName(name).getClass() == Committee)
	    return -2;
    	def comm = new Committee(name: name, description: desc, bylaws: bylaws);
    	if (!(comm).save(flush: true))
	    return -1
	return 0;
    }

    @Transactional
    def updateCommittee(name, desc, bylaws) {
    	def comm = Committee.findByName(name);
    	if (comm.getClass() != Committee)
	    return -2;
	    comm.name = name;
	    comm.description = desc;
	    comm.bylaws = bylaws;
	if (!(comm.save(flush: true)))
	    return -1;
	return 0;
    }

    @Transactional
    def deleteCommittee(name) {
    	def cmems = CommitteeMember.findAllByCommittee(Committee.findByName(name));
	for (cmem in cmems)
	    cmem.delete();
    	Committee.findByName(name).delete();
    }

    @Transactional
    def saveMember(fname, mname, lname, address, phone) {
    	if (Member.findByLastNameAndFirstName(lname, fname).getClass() == Member)
	    return -2;
    	def mem = new Member(firstName: fname, middleName: mname, lastName: lname, address: address, phone: phone);
	if (!(mem.save(flush: true)))
	    return -1;
	return 0;
    }

    @Transactional
    def updateMember(oldLastName, oldFirstName, fname, mname, lname, address, phone) {
    	def mem = Member.findByLastNameAndFirstName(oldLastName, oldFirstName);
	    mem.firstName = fname;
	    mem.middleName = mname;
	    mem.lastName = lname;
	    mem.address = address;
	    mem.phone = phone;
	if (!(mem.save(flush: true)))
	    return -1;
	return 0;
    }

    @Transactional
    def deleteMember(fname, lname) {
    	def memcs = CommitteeMember.findAllByMember(Member.findByLastNameAndFirstName(lname, fname));
	for (memc in memcs)
	    memc.delete();
	Member.findByLastNameAndFirstName(lname, fname).delete();
    }

    @Transactional
    def saveCommitteeMember(cname, lname, fname, title, start, end) {
    	if (CommitteeMember.findByMemberAndCommittee(Member.findByLastNameAndFirstName(lname, fname), Committee.findByName(cname)).getClass() == CommitteeMember)
	    return -3;
    	def cmem = new CommitteeMember(title: title, startDate: start, endDate: end);
	cmem.member = Member.findByLastNameAndFirstName(lname, fname);
	if (cmem.member.getClass() != Member)
	    return -2;
	cmem.committee = Committee.findByName(cname);
	if (cmem.committee.getClass() != Committee)
	    return -2;
	if (!(cmem.save(flush: true)))
	    return -1;
	return 0;
    }

    @Transactional    
    def updateCommitteeMember(member, cname, title, startDate, endDate) {
        def cmem = CommitteeMember.findByMemberAndCommittee(member, Committee.findByName(cname));
	    cmem.title = title;
	    cmem.startDate = startDate;
	    cmem.endDate = endDate;
	    cmem.updated = new Date();
	if (!cmem.save(flush: true))
	    return -1;
    }
    
    @Transactional
    def deleteCommitteeMember(member, cname) {
        CommitteeMember.findByMemberAndCommittee(member, Committee.findByName(cname)).delete();
    }

    @Transactional
    def saveUser(username, password, auth) {
    	if (User.findByUsername(username).getClass() == User)
	   return -2;
	def urole;
        def u = new User(username: username, password: password);
	if (!(u.save(flush: true)))
	   return -1;
	if (auth == "Admin")
	   urole = new UserRole(user: u, role: Role.findByAuthority("ROLE_ADMIN"));
	else
	   urole = new UserRole(user: u, role: Role.findByAuthority("ROLE_USER"));
	if (!(urole.save(flush: true)))
	   return -1;
	return 0;
    }
	
	

    @Transactional
    def updateUser(user, newPass, newRole) {
    	UserRole.findByUser(user).delete();
	if (newPass != "") {
    	    def u = urole.user;
	    	u.password = newPass;
	    if (!(u.save(flush: true)))
	        return -1;
	    
	}
	
	if (newRole == "Admin")
	    newRole = "ROLE_ADMIN";
	else
	    newRole = "ROLE_USER";
	if (!(new UserRole(user: user, role: Role.findByAuthority(newRole)).save(flush: true)))
	   return -1;	
	return 0;
    }

    def deleteUser(user) {
        UserRole.findByUser(user).delete();
	User.findByUsername(user.username).delete();
    }
}
