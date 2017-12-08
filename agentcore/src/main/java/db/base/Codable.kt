package db.base

import java.util.*

/**
 * @author Nikita Gorodilov
 */
interface Codable<out S> {

    val code: S

    companion object {
        fun <T, S> find(codableSet: Class<T>, code: S): T where T : Enum<T>, T : Codable<S>, S : Any {
            return tryFind(codableSet, code).orElseThrow { IllegalArgumentException(String.format("%s type not found for code = %s", *arrayOf<Any>(codableSet.name, code))) }
        }

        fun <T, S> find(codableSet: Class<T>, code: S, defaultValue: T): T where T : Enum<T>, T : Codable<S> {
            return tryFind(codableSet, code).orElse(defaultValue)
        }

        fun <T, S> tryFind(codableSet: Class<T>, code: S): Optional<T> where T : Enum<T>, T : Codable<S> {
            return Arrays.stream(codableSet.enumConstants).filter { codable -> (codable as Codable<*>).code == code }.findFirst()
        }
    }
}