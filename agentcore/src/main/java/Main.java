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

        LoginServiceImpl loginService = (LoginServiceImpl) context.getBean(LoginService.class);;

        String agentMasId = UUID.randomUUID().toString();
        Agent agent = loginService.registration(new RegistrationData(
                agentMasId,
                UUID.randomUUID().toString(),
                "worker",
                "psw")
        );
        agent = loginService.login(new LoginData(
                agentMasId,
                "psw"
        ));
        loginService.logout();

        agentMasId = UUID.randomUUID().toString();
        agent = loginService.registration(new RegistrationData(
                agentMasId,
                UUID.randomUUID().toString(),
                "worker",
                "psw")
        );
        agent = loginService.login(new LoginData(
                agentMasId,
                "psw"
        ));
        loginService.logout();
    }
}
