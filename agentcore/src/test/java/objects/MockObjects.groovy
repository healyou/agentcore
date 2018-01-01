package objects

import org.easymock.Capture
import org.easymock.IAnswer
import com.mycompany.service.LoginService
import com.mycompany.service.ServerTypeService
import com.mycompany.service.SessionManager
import com.mycompany.service.objects.Agent
import com.mycompany.service.objects.LoginData
import com.mycompany.service.objects.RegistrationData

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

        expect(loginService.login(capture(loginDataCapture), capture(sessionManagerCapture))).andStubAnswer(new IAnswer<Agent>() {
            @Override
            Agent answer() throws Throwable {
                OtherObjects.agent(loginDataCapture.value)
            }
        })
        expect(loginService.registration(capture(registrationDataCapture), capture(sessionManagerCapture))).andStubAnswer(new IAnswer<Agent>() {
            @Override
            Agent answer() throws Throwable {
                OtherObjects.agent(registrationDataCapture.value)
            }
        })
        expect(loginService.logout(capture(sessionManagerCapture))).andStubReturn(true)
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
        expect(serverTypeService.getMessageTypes(capture(sessionManagerCapture)))
                .andStubReturn(TypesObjects.messageTypes)
        replay(serverTypeService)

        serverTypeService
    }
}
