import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.*;
import service.objects.Agent;
import service.objects.GetAgentsData;
import service.objects.LoginData;
import service.objects.RegistrationData;

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
        testServerAgentService(context, sessionManager);
    }

    private static void testServerAgentService(ApplicationContext context,  SessionManager sessionManager) {
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
