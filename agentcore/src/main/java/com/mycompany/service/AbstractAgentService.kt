package com.mycompany.service

import com.mycompany.db.base.Environment
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.io.IOException
import java.nio.charset.Charset
import java.util.stream.Collectors

/**
 * @author Nikita Gorodilov
 */
abstract class AbstractAgentService {

    protected val restTemplate: RestTemplate = RestTemplate()
    /* Базовый адрес сервера */
    protected abstract val BASE_URL: String
    /* Загрузка параметров из бд */
    protected abstract val environment: Environment

    init {
        val formHttpMessageConverter = FormHttpMessageConverter()
        val stringHttpMessageConverter = StringHttpMessageConverter(Charset.forName("UTF-8"))
        val list = mutableListOf<HttpMessageConverter<*>>()
        list.add(formHttpMessageConverter)
        list.add(stringHttpMessageConverter)
        restTemplate.messageConverters = list
    }

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

    companion object {

        @Throws(IOException::class)
        fun <T> fromJson(json: String, tClass: Class<T>): T {
            val mapper = ObjectMapper()
            return mapper.readValue(json, tClass)
        }

        @Throws(IOException::class)
        @JvmStatic
        fun <T> fromJson(json: String, tReference: TypeReference<T>): T {
            val mapper = ObjectMapper()
            return mapper.readValue(json, tReference)
        }

        @Throws(JsonProcessingException::class)
        fun toJson(obj: Any): String {
            val mapper = ObjectMapper()
            return mapper.writeValueAsString(obj)
        }
    }
}