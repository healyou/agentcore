package service

import service.objects.Agent
import service.objects.LoginData
import service.objects.RegistrationData

/**
 * @author Nikita Gorodilov
 */
interface LoginService {

    fun registration(registrationData: RegistrationData, sessionManager: SessionManager): Agent?

    fun login(loginData: LoginData, sessionManager: SessionManager): Agent?

    fun logout(sessionManager: SessionManager): Boolean
}