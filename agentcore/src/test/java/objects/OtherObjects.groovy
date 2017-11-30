package objects

import dsl.RuntimeAgentServiceTest
import dsl.objects.DslImage

import javax.imageio.ImageIO
import java.awt.Image
import java.awt.image.BufferedImage

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
}
