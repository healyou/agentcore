package service

import agentcore.database.base.Environment
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.io.IOException
import java.util.stream.Collectors

/**
 * @author Nikita Gorodilov
 */
abstract class AbstractAgentService {

    /* Базовый адрес сервера */
    protected abstract val BASE_URL: String
    /* Загрузка параметров из бд */
    protected abstract val environment: Environment

    /**
     * Header одинаковый у всех сообщений
     */
    protected fun createHttpHeaders(sessionManager: SessionManager): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        if (!sessionManager.cookie.isEmpty()) {
            headers.set("Cookie", sessionManager.cookie.stream().collect(Collectors.joining(";")))
        }
        return headers
    }

    @Throws(IOException::class)
    protected fun <T> fromJson(json: String, tClass: Class<T>): T {
        val mapper = ObjectMapper()
        return mapper.readValue(json, tClass)
    }

    @Throws(JsonProcessingException::class)
    protected fun toJson(`object`: Any): String {
        val mapper = ObjectMapper()
        return mapper.writeValueAsString(`object`)
    }
}