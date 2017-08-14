import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.LoginService;
import service.LoginServiceImpl;
import service.SessionManager;
import service.objects.Agent;
import service.objects.LoginData;
import service.objects.RegistrationData;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Nikita Gorodilov
 */
public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");

        // todo исправить путь до бд в properties
        // todo передача coocke manager в качестве параметра
        LoginServiceImpl loginService = (LoginServiceImpl) context.getBean(LoginService.class);;
        SessionManager sessionManager = new SessionManager();

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
        loginService.logout(sessionManager);

        agentMasId = UUID.randomUUID().toString();
        agent = loginService.registration(
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
        loginService.logout(sessionManager);
    }
}
