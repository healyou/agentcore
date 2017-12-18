package objects

import db.base.ExtensionsKt
import dsl.RuntimeAgentServiceTest
import dsl.objects.DslImage

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

    /**
     * Формат даты описан в Extensions.kt
     */
    static String getSqliteDateString(Date date) {
        new SimpleDateFormat(ExtensionsKt.SQLITE_DATE_FORMAT).format(date)
    }
}
