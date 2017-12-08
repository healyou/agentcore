import com.company.AgentImageFunctions;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.*;
import service.objects.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Nikita Gorodilov
 */
public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        SessionManager sessionManager = new SessionManager();
        // todo исправить путь до бд в properties
        testLoginService(context, sessionManager);
        testServerTypeService(context, sessionManager);
    }

    private static void testServerTypeService(ApplicationContext context, SessionManager sessionManager) {
        ServerTypeServiceImpl serverTypeService = (ServerTypeServiceImpl) context.getBean(ServerTypeService.class);

        List<AgentType> agentTypes = serverTypeService.getAgentTypes(sessionManager);
        List<MessageBodyType> messageBodyTypes = serverTypeService.getMessageBodyTypes(sessionManager);
        List<MessageGoalType> messageGoalTypes = serverTypeService.getMessageGoalTypes(sessionManager);
        List<MessageType> messageTypes = serverTypeService.getMessageTypes(sessionManager, messageGoalTypes.get(0).getCode().getCode());
        List<MessageType> messageTypeList = serverTypeService.getMessageTypes(sessionManager);

        int k = 1;
    }

    private static void testFunctions() {
        AgentImageFunctions.testImageFun1();
        AgentImageFunctions.testImageFun2();
        AgentImageFunctions.testImageFun3();
    }

    private static void testServiceFuncions() {
        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        SessionManager sessionManager = new SessionManager();
        // todo исправить путь до бд в properties
        testLoginService(context, sessionManager);
        testServerAgentService(context, sessionManager);
        testSendMessageService(context, sessionManager);
    }

    private static void testSendMessageService(ApplicationContext context, SessionManager sessionManager) {
        ServerMessageServiceImpl serverMessageService = (ServerMessageServiceImpl) context.getBean(ServerMessageService.class);

        ServerAgentServiceImpl serverAgentService = (ServerAgentServiceImpl) context.getBean(ServerAgentService.class);
        Agent agent = serverAgentService.getCurrentAgent(sessionManager);

        List<Long> recipients = new ArrayList<>();
        recipients.add(agent.getId());

        Message message = serverMessageService.sendMessage(sessionManager, new SendMessageData(
                MessageGoalType.Code.TASK_DECISION.getCode(),
                MessageType.Code.SEARCH_SOLUTION.getCode(),
                recipients,
                MessageBodyType.Code.JSON.getCode(),
                "{}"
        ));
        List<Message> list = serverMessageService.getMessages(sessionManager, new GetMessagesData(
                null,
                null,
                null,
                null,
                false,
                null,
                null
        ));
        int k = 1;
    }

    private static void testServerAgentService(ApplicationContext context, SessionManager sessionManager) {
        ServerAgentServiceImpl serverAgentService = (ServerAgentServiceImpl) context.getBean(ServerAgentService.class);

        Agent agent = serverAgentService.getCurrentAgent(sessionManager);
        List<Agent> agents = serverAgentService.getAgents(sessionManager, new GetAgentsData(
                "worker",
                null
        ));
        int k = 1;
    }

    private static void testLoginService(ApplicationContext context, SessionManager sessionManager) {
        LoginServiceImpl loginService = (LoginServiceImpl) context.getBean(LoginService.class);

        String agentMasId = UUID.randomUUID().toString();
        Agent agent = loginService.registration(
                new RegistrationData(
                        agentMasId,
                        UUID.randomUUID().toString(),
                        "worker",
                        "psw"
                ),
                sessionManager
        );
        agent = loginService.login(
                new LoginData(
                        agentMasId,
                        "psw"),
                sessionManager
        );
        //loginService.logout(sessionManager);

//        agentMasId = UUID.randomUUID().toString();
//        agent = loginService.registration(
//                new RegistrationData(
//                        agentMasId,
//                        UUID.randomUUID().toString(),
//                        "worker",
//                        "psw"
//                ),
//                sessionManager
//        );
//        agent = loginService.login(
//                new LoginData(
//                        agentMasId,
//                        "psw"),
//                sessionManager
//        );
        //loginService.logout(sessionManager);
    }
}
