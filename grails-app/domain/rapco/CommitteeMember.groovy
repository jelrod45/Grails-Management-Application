package rapco

class CommitteeMember {

    Member member
    Committee committee
    Date startDate
    Date endDate
    String title
    Date created = new Date()
    Date updated = new Date()

    static constraints = {
    	startDate blank: false, nullable: false
    	endDate blank: false, nullable: false
	title blank: false, nullable: false, matches: "[a-zA-Z0-9 '!@#&()':/+]*"
    	created blank: false, nullable: false, editable: false, display: false
    	updated blank: false, nullable: false, editable: false, display: false
    }

    static mapping = {
        member lazy: false
	committee lazy: false
    }

}
