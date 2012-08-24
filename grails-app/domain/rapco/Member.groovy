package rapco

class Member {
    String firstName
    String middleName
    String lastName
    String address
    String phone

    static constraints = {
        firstName blank: false, nullable: false, matches: "[a-zA-Z]*"
        middleName blank: false, nullable: false, matches: "[a-zA-Z]*"
        lastName blank: false, nullable: false, matches: "[a-zA-Z]*"
	address blank: false, nullable: false, matches: "[0-9a-zA-Z \n,.!?@&()\\-]*"
	phone blank: false, nullable: false, matches: "[0-9 (-]*"
    }

}
