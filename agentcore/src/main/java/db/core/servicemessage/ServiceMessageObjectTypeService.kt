package db.core.servicemessage

/**
 * @author Nikita Gorodilov
 */
interface ServiceMessageObjectTypeService {

    fun get(code: ServiceMessageObjectType.Code): ServiceMessageObjectType
}