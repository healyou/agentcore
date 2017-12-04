package objects

import org.easymock.Capture
import service.LoginService
import service.ServerTypeService
import service.SessionManager
import service.objects.LoginData
import service.objects.RegistrationData

import static org.easymock.EasyMock.*

/**
 * @author Nikita Gorodilov
 */
class MockObjects {

    static LoginService loginService() {
        def loginService = mock(LoginService.class)

        Capture<RegistrationData> registrationDataCapture = newCapture()
        Capture<SessionManager> sessionManagerCapture = newCapture()
        Capture<LoginData> loginDataCapture = newCapture()

        expect(loginService.login(capture(loginDataCapture), capture(sessionManagerCapture))).andStubReturn(null)
        expect(loginService.registration(capture(registrationDataCapture), capture(sessionManagerCapture))).andStubReturn(null)
        replay(loginService)

        loginService
    }

    static ServerTypeService serverTypeService() {
        def serverTypeService = mock(ServerTypeService.class)
        Capture<SessionManager> sessionManagerCapture = newCapture()
        Capture<String> goalTypeCapture = newCapture()
        def messageGoalTypes = TypesObjects.messageGoalTypes

        expect(serverTypeService.getAgentTypes(capture(sessionManagerCapture)))
                .andStubReturn(TypesObjects.agentTypes)
        expect(serverTypeService.getMessageBodyTypes(capture(sessionManagerCapture)))
                .andStubReturn(TypesObjects.messageBodyTypes)
        expect(serverTypeService.getMessageGoalTypes(capture(sessionManagerCapture)))
                .andStubReturn(messageGoalTypes)
        expect(serverTypeService.getMessageTypes(capture(sessionManagerCapture), capture(goalTypeCapture)))
                .andStubReturn(TypesObjects.messageTypes)
        replay(serverTypeService)

        serverTypeService
    }
}
