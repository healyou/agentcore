package service

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

    /**
     * Адрес сервиса
     */
    // todo засунуть в environment
    companion object {
        val BASE_URL = "http://127.0.0.1:9999/"
    }
    /* Одни куки на все сервисы */
    protected abstract val sessionManager: SessionManager

    /**
     * Header одинаковый у всех сообщений
     */
    protected fun createHttpHeaders(): HttpHeaders {
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