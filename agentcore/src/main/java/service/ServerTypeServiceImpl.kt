package service

import com.fasterxml.jackson.core.type.TypeReference
import db.base.Environment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import service.objects.AgentType
import service.objects.MessageBodyType
import service.objects.MessageGoalType
import service.objects.MessageType

/**
 * @author Nikita Gorodilov
 */
@Component
open class ServerTypeServiceImpl(@Autowired final override val environment: Environment) : AbstractAgentService(), ServerTypeService {

    override val BASE_URL: String = environment.getProperty("agent.service.base.url")
    private val GET_AGENT_TYPES_URL = environment.getProperty("agent.service.type.get.agent.types.url")
    private val GET_MESSAGE_BODY_TYPES_URL = environment.getProperty("agent.service.type.get.message.body.types.url")
    private val GET_MESSAGE_GOAL_TYPES_URL = environment.getProperty("agent.service.type.get.message.goal.types.url")
    private val GET_MESSAGE_TYPES_BY_GOAL_TYPE_URL = environment.getProperty("agent.service.type.get.message.types.by.goal.type.url")
    private val GET_MESSAGE_TYPES_URL = environment.getProperty("agent.service.type.get.message.types.url")

    override fun getAgentTypes(sessionManager: SessionManager): List<AgentType>? =
            getTypes(sessionManager, GET_AGENT_TYPES_URL, object : TypeReference<List<AgentType>>(){})

    override fun getMessageBodyTypes(sessionManager: SessionManager): List<MessageBodyType>? =
            getTypes(sessionManager, GET_MESSAGE_BODY_TYPES_URL, object : TypeReference<List<MessageBodyType>>(){})

    override fun getMessageGoalTypes(sessionManager: SessionManager): List<MessageGoalType>? =
            getTypes(sessionManager, GET_MESSAGE_GOAL_TYPES_URL, object : TypeReference<List<MessageGoalType>>(){})

    override fun getMessageTypes(sessionManager: SessionManager, goalType: String): List<MessageType>? {
        return try {
            val map = LinkedMultiValueMap<String, String>()
            map.add("goalType", goalType)

            val request = HttpEntity<MultiValueMap<String, String>>(map, createHttpHeaders(sessionManager))

            val outData = restTemplate.exchange(BASE_URL + GET_MESSAGE_TYPES_BY_GOAL_TYPE_URL, HttpMethod.POST, request, String::class.java)
            val jsonObject = outData.body

            fromJson(jsonObject, object : TypeReference<List<MessageType>>(){})
        } catch (e: Exception) {
            null
        }
    }

    override fun getMessageTypes(sessionManager: SessionManager): List<MessageType>? =
            getTypes(sessionManager, GET_MESSAGE_TYPES_URL, object : TypeReference<List<MessageType>>(){})

    /**
     * Получение списка объектов в зависимости от addUrl
     */
    private fun <T> getTypes(sessionManager: SessionManager, addUrl: String, tReference: TypeReference<List<T>>): List<T>? {
        return try {
            val request = HttpEntity<MultiValueMap<String, String>>(createHttpHeaders(sessionManager))

            val outData = restTemplate.exchange(BASE_URL + addUrl, HttpMethod.GET, request, String::class.java)
            val jsonObject = outData.body

            fromJson(jsonObject, tReference)
        } catch (e: Exception) {
            null
        }
    }
}