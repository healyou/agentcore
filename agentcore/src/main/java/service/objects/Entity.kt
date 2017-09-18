package service.objects

/**
 * @author Nikita Gorodilov
 */
interface Entity {

    var id: Long?

    val isNew
        get() = id != null
}