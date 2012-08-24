package rapco

class CommitteeMemberController {

    def scaffold = CommitteeMember

    def index = {
        def cm = CommitteeMember.get(params.id)
	cm.updated = new Date()
	cm.save()
    }

}
