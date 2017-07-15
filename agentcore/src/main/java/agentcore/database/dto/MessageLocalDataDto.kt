package agentcore.database.dto

/**
 * Объект содержит всю информацию о лок бд
 * нужен для передачи информации по сети без привязки к реальным объектам бд
 *
 * @author Nikita Gorodilov
 */
class MessageLocalDataDto(paramType: HashMap<String, String>,
                   paramValue: HashMap<String, Any>)
    : ALocalDataDto(paramType, paramValue) {

    companion object {

        fun valueOf(messageDto: LocalDataDto): MessageLocalDataDto {
            return MessageLocalDataDto(messageDto.paramType, messageDto.paramValue)
        }
    }
}