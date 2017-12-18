package objects

import service.objects.*

/**
 * @author Nikita Gorodilov
 */
class RestServiceObjects {

    static def registrationData(String password) {
        new RegistrationData(
                StringObjects.randomString(),
                StringObjects.emptyString(),
                TypesObjects.testAgentType1().code,
                password
        )
    }

    static def loginData(RegistrationData registrationData) {
        new LoginData(
                registrationData.masId,
                registrationData.password
        )
    }

    static def loginData(String masId, String password) {
        new LoginData(masId, password)
    }

    static def getAgentsData() {
        new GetAgentsData()
    }

    static def getMessageData() {
        new GetMessagesData()
    }

    static def getMessageData(Long senderId) {
        new GetMessagesData(
                null,
                null,
                null,
                senderId,
                null,
                null,
                null
        )
    }

    static def sendMessageData(String messageType, List<Long> recipientsIds, String messageBodyType, String messageBody) {
        new SendMessageData(
                messageType,
                recipientsIds,
                messageBodyType,
                messageBody
        )
    }

    static def randomDataSendMessageData() {
        new SendMessageData(
                StringObjects.randomString(),
                Collections.emptyList(),
                StringObjects.randomString(),
                StringObjects.randomString()
        )
    }
}
