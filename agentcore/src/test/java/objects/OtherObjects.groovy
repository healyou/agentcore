package objects

import com.mycompany.db.base.ExtensionsKt
import com.mycompany.db.core.file.ByteArrayFileContent
import com.mycompany.db.core.file.FileContent
import com.mycompany.db.core.file.FileContentLocator
import com.mycompany.db.core.file.FileContentRef
import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.dsl.objects.DslServiceMessage
import com.mycompany.service.objects.Agent
import com.mycompany.service.objects.AgentType
import com.mycompany.service.objects.LoginData
import com.mycompany.service.objects.RegistrationData
import com.mycompany.dsl.objects.DslImage
import org.jetbrains.annotations.NotNull

import java.text.SimpleDateFormat

/**
 * @author Nikita Gorodilov
 */
class OtherObjects {

    static def dslFileAttachment(String filename, byte[] content) {
        new DslFileAttachment(
                filename,
                new FileContentRef() {
                    @Override
                    FileContent getContent(@NotNull FileContentLocator visitor) {
                        return new ByteArrayFileContent(content)
                    }
                    @Override
                    String getName() {
                        return filename
                    }
                },
                content.length
        )
    }

    static DslServiceMessage dslServiceMessage(senderCode) {
        new DslServiceMessage(senderCode, StringObjects.randomString())
    }

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
