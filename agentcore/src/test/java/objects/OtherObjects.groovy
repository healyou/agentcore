package objects

import db.base.ExtensionsKt
import dsl.RuntimeAgentServiceTest
import dsl.objects.DslImage
import service.objects.Agent
import service.objects.AgentType
import service.objects.LoginData
import service.objects.RegistrationData

import javax.imageio.ImageIO
import java.awt.Image
import java.awt.image.BufferedImage
import java.text.SimpleDateFormat

/**
 * @author Nikita Gorodilov
 */
class OtherObjects {

    static DslImage image() {
        new DslImage(
                StringObjects.randomString(),
                [1,2,3] as byte[]
        )
    }

    static String emptyJsonObject() {
        "{}"
    }

    static Date getDate(int year, int month, int day) {
        new Date(year, month, day)
    }

    static Agent agent(LoginData loginData) {
        new Agent(
                nextId(),
                loginData.masId,
                StringObjects.randomString(),
                TypesObjects.testAgentType1(),
                new Date(System.currentTimeMillis()),
                false
        )
    }

    static Agent agent(RegistrationData registrationData) {
        new Agent(
                nextId(),
                registrationData.masId,
                registrationData.name,
                agentType(registrationData.type),
                new Date(System.currentTimeMillis()),
                false
        )
    }

    static AgentType agentType(String code) {
        new AgentType(
                nextId(),
                code,
                StringObjects.randomString(),
                false
        )
    }

    /**
     * Формат даты описан в Extensions.kt
     */
    static String getSqliteDateString(Date date) {
        new SimpleDateFormat(ExtensionsKt.SQLITE_DATE_FORMAT).format(date)
    }

    private static Long id = 0L;
    static Long nextId() {
        id++;
    }
}
