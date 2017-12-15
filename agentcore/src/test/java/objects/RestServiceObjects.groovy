package objects

import service.objects.GetAgentsData
import service.objects.GetMessagesData
import service.objects.LoginData
import service.objects.RegistrationData
import service.objects.SendMessageData

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

    static def getAgentsData() {
        new GetAgentsData()
    }

    static def getMessageData() {
        new GetMessagesData()
    }

    static def sendMessageData(String type, List<Long> recipientsIds, String bodyType, String body) {
        new SendMessageData(
                type,
                recipientsIds,
                bodyType,
                body
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
