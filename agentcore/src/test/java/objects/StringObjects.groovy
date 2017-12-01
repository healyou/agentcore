package objects

/**
 * @author Nikita Gorodilov
 */
class StringObjects {

    static String randomString() {
        UUID.randomUUID().toString()
    }

    static def emptyString() {
        ""
    }
}
