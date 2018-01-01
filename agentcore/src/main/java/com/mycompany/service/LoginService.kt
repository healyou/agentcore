package com.mycompany.service

import com.mycompany.service.objects.Agent
import com.mycompany.service.objects.LoginData
import com.mycompany.service.objects.RegistrationData

/**
 * @author Nikita Gorodilov
 */
interface LoginService {

    fun registration(registrationData: RegistrationData, sessionManager: SessionManager): Agent?

    fun login(loginData: LoginData, sessionManager: SessionManager): Agent?

    fun logout(sessionManager: SessionManager): Boolean
}