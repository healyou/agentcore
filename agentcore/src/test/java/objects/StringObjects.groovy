package objects

/**
 * @author Nikita Gorodilov
 */
class StringObjects {

    static def randomString() {
        UUID.randomUUID().toString()
    }

    static def emptyString() {
        ""
    }
}
