package rapco

class Committee {
    String name
    String description
    String bylaws

    static constraints = {
        name blank: false, nullable: false, matches: "[a-zA-Z0-9 '()&%#@!./,:]*", unique: true
        description blank: false, nullable: false, matches: "[a-zA-Z0-9 '()&%#@!./,:]*"
        bylaws blank: false, nullable: false, matches: "[a-zA-Z0-9 '()&%#@!./,:]*"
   }
}
