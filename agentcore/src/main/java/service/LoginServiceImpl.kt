package service

import agentcore.database.base.Environment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import service.objects.Agent
import service.objects.LoginData
import service.objects.RegistrationData
import java.nio.charset.Charset

/**
 * @author Nikita Gorodilov
 */
@Component
open class LoginServiceImpl(@Autowired final override val environment: Environment) : AbstractAgentService(), LoginService {

    override val BASE_URL: String = environment.getProperty("agent.service.base.url")
    private val LOGIN_URL = environment.getProperty("agent.service.login.login.url")
    private val REGISTRATION_URL = environment.getProperty("agent.service.login.registration.url")
    private val LOGOUT_URL = environment.getProperty("agent.service.login.logout.url")

    override fun registration(registrationData: RegistrationData, sessionManager: SessionManager): Agent? {
        try {
            val map = LinkedMultiValueMap<String, String>()
            map.add("masId", registrationData.masId)
            map.add("name", registrationData.name)
            map.add("type", registrationData.type)
            map.add("password", registrationData.password)

            val request = HttpEntity<MultiValueMap<String, String>>(map, createHttpHeaders(sessionManager))

            val outLoginData = restTemplate.exchange(BASE_URL + REGISTRATION_URL, HttpMethod.POST, request, String::class.java)
            val jsonObject = outLoginData.body

            /* грузим куки, если они есть */
            try {
                sessionManager.cookie = outLoginData.headers["Set-Cookie"]!!
            }catch (e: Exception) {
            }

            return fromJson(jsonObject, Agent::class.java)
        } catch (e: Exception) {
            return null
        }
    }

    override fun login(loginData: LoginData, sessionManager: SessionManager): Agent? {
        try {
            val map = LinkedMultiValueMap<String, String>()
            map.add("masId", loginData.masId)
            map.add("password", loginData.password)

            val request = HttpEntity(map, createHttpHeaders(sessionManager))

            val outLoginData = restTemplate.exchange(BASE_URL + LOGIN_URL, HttpMethod.POST, request, String::class.java)
            val jsonObject = outLoginData.body

            /* грузим куки, если они есть */
            try {
                sessionManager.cookie = outLoginData.headers["Set-Cookie"]!!
            }catch (e: Exception) {
            }

            return fromJson(jsonObject, Agent::class.java)
        } catch (e: Exception) {
            return null
        }
    }

    override fun logout(sessionManager: SessionManager): Boolean {
        try {
            val entity = HttpEntity<Any>(createHttpHeaders(sessionManager))

            restTemplate.exchange(BASE_URL + LOGOUT_URL, HttpMethod.GET, entity, String::class.java)
            return true
        } catch (e: Exception) {
            return false
        }
    }
}