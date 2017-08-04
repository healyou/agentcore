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
        LoginServiceImpl loginService = new LoginServiceImpl(new SessionManager());

        String agentMasId = UUID.randomUUID().toString();
        Agent agent = loginService.registration(new RegistrationData(
                agentMasId,
                UUID.randomUUID().toString(),
                "worker",
                "psw")
        );
        agent = loginService.login(new LoginData(
                "masId5",
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
                "masId5",
                "psw"
        ));
        loginService.logout();
    }
}
