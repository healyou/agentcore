package service

import db.base.Environment
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import service.objects.Agent
import service.objects.GetAgentsData

/**
 * @author Nikita Gorodilov
 */
@Component
open class ServerAgentServiceImpl(@Autowired final override val environment: Environment) : AbstractAgentService(), ServerAgentService {

    override val BASE_URL: String = environment.getProperty("agent.service.base.url")
    private val GET_CURRENT_AGENT_URL = environment.getProperty("agent.service.agent.get.current.agent.url")
    private val GET_AGENTS_URL = environment.getProperty("agent.service.agent.get.agents.url")
    private val GET_AGENT_URL = environment.getProperty("agent.service.agent.get.agent.url")

    override fun getCurrentAgent(sessionManager: SessionManager): Agent? {
        return try {
            val entity = HttpEntity<Any>(createHttpHeaders(sessionManager))

            val outData = restTemplate.exchange(BASE_URL + GET_CURRENT_AGENT_URL, HttpMethod.POST, entity, String::class.java)
            val jsonObject = outData.body

            /* грузим куки, если они есть */
            fromJson(jsonObject, Agent::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override fun getAgents(sessionManager: SessionManager, data: GetAgentsData): List<Agent>? {
        return try {
            val map = LinkedMultiValueMap<String, String>()
            if (data.type != null) {
                map.add("type", data.type)
            }
            if (data.isDeleted != null) {
                map.add("isDeleted", data.isDeleted.toString())
            }
            if (data.name != null) {
                map.add("name", data.name)
            }

            val request = HttpEntity<MultiValueMap<String, String>>(map, createHttpHeaders(sessionManager))

            val outLoginData = restTemplate.exchange(BASE_URL + GET_AGENTS_URL, HttpMethod.POST, request, String::class.java)
            val jsonObject = outLoginData.body

            /* грузим куки, если они есть */
            fromJson(jsonObject, object : TypeReference<List<Agent>>(){})
        } catch (e: Exception) {
            null
        }
    }

    override fun getAgent(sessionManager: SessionManager, masId: String): Agent? {
        return try {
            val map = LinkedMultiValueMap<String, String>()
            map.add("masId", masId)

            val request = HttpEntity<MultiValueMap<String, String>>(map, createHttpHeaders(sessionManager))

            val outData = restTemplate.exchange(BASE_URL + GET_AGENT_URL, HttpMethod.POST, request, String::class.java)
            val jsonObject = outData.body

            fromJson(jsonObject, object : TypeReference<Agent>(){})
        } catch (e: Exception) {
            null
        }
    }
}