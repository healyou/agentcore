package service

import db.base.Environment
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import service.objects.GetMessagesData
import service.objects.Message
import service.objects.SendMessageData

/**
 * @author Nikita Gorodilov
 */
@Component
open class ServerMessageServiceImpl(@Autowired final override val environment: Environment) : AbstractAgentService(), ServerMessageService {

    override val BASE_URL: String = environment.getProperty("agent.service.base.url")
    private val SEND_MESSAGE_URL = environment.getProperty("agent.service.message.send.message.url")
    private val GET_MESSAGES_URL = environment.getProperty("agent.service.message.get.messages.url")

    override fun sendMessage(sessionManager: SessionManager, data: SendMessageData): Message? {
        return try {
            val map = LinkedMultiValueMap<String, String>()
            map.add("goalType", data.goalType)
            map.add("type", data.type)
            map.add("bodyType", data.bodyType)
            map.add("body", data.body)
            data.recipientsIds.forEach {
                map.add("recipientsIds", it.toString())
            }

            val request = HttpEntity<MultiValueMap<String, String>>(map, createHttpHeaders(sessionManager))

            val outData = restTemplate.exchange(BASE_URL + SEND_MESSAGE_URL, HttpMethod.POST, request, String::class.java)
            val jsonObject = outData.body

            /* грузим куки, если они есть */
            fromJson(jsonObject, Message::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override fun getMessages(sessionManager: SessionManager, data: GetMessagesData): List<Message>? {
        return try {
            val map = LinkedMultiValueMap<String, String>()
            if (data.goalType != null) {
                map.add("goalType", data.goalType)
            }
            if (data.type != null) {
                map.add("type", data.type)
            }
            if (data.bodyType != null) {
                map.add("bodyType", data.bodyType)
            }
            if (data.senderId != null) {
                map.add("senderId", data.senderId.toString())
            }
            if (data.isViewed != null) {
                map.add("isViewed", data.isViewed.toString())
            }
            if (data.sinceCreatedDate != null) {
                map.add("sinceCreatedDate", data.sinceCreatedDate.toString())
            }
            if (data.sinceViewedDate != null) {
                map.add("sinceViewedDate", data.sinceViewedDate.toString())
            }

            val request = HttpEntity<MultiValueMap<String, String>>(map, createHttpHeaders(sessionManager))

            val outData = restTemplate.exchange(BASE_URL + GET_MESSAGES_URL, HttpMethod.POST, request, String::class.java)
            val jsonObject = outData.body

            /* грузим куки, если они есть */
            fromJson(jsonObject, object : TypeReference<List<Message>>(){})
        } catch (e: Exception) {
            null
        }
    }
}