package service

import service.objects.Agent
import service.objects.LoginData
import service.objects.RegistrationData

/**
 * @author Nikita Gorodilov
 */
interface LoginService {

    fun registration(registrationData: RegistrationData): Agent?

    fun login(loginData: LoginData): Agent?

    fun logout(): Boolean
}