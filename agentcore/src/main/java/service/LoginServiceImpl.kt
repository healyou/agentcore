package service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import service.objects.Agent
import service.objects.LoginData
import service.objects.RegistrationData
import java.nio.charset.Charset

/**
 * @Scope("prototype") -> тк для каждого объекта будут свои куки
 *
 * @author Nikita Gorodilov
 */
@Service()
@Scope("prototype")
class LoginServiceImpl(@Autowired override val sessionManager: SessionManager) : AbstractAgentService(), LoginService {

    companion object {
        private val LOGIN_URL = "login/login"
        private val REGISTRATION_URL = "login/registration"
        private val LOGOUT_URL = "login/logout"
    }

    private var restTemplate: RestTemplate = RestTemplate()

    init {
        val formHttpMessageConverter = FormHttpMessageConverter()
        val stringHttpMessageConverter = StringHttpMessageConverter(Charset.forName("UTF-8"))
        val list = mutableListOf<HttpMessageConverter<*>>()
        list.add(formHttpMessageConverter)
        list.add(stringHttpMessageConverter)
        restTemplate.messageConverters = list
    }

    override fun registration(registrationData: RegistrationData): Agent? {
        try {
            val map = LinkedMultiValueMap<String, String>()
            map.add("masId", registrationData.masId)
            map.add("name", registrationData.name)
            map.add("type", registrationData.type)
            map.add("password", registrationData.password)

            val request = HttpEntity<MultiValueMap<String, String>>(map, createHttpHeaders())

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

    override fun login(loginData: LoginData): Agent? {
        try {
            val map = LinkedMultiValueMap<String, String>()
            map.add("masId", loginData.masId)
            map.add("password", loginData.password)

            val request = HttpEntity(map, createHttpHeaders())

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

    override fun logout(): Boolean {
        try {
            val entity = HttpEntity<Any>(createHttpHeaders())

            restTemplate.exchange(BASE_URL + LOGOUT_URL, HttpMethod.GET, entity, String::class.java)
            return true
        } catch (e: Exception) {
            return false
        }
    }
}