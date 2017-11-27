package objects

import dsl.RuntimeAgentServiceTest

import javax.imageio.ImageIO
import java.awt.Image
import java.awt.image.BufferedImage

/**
 * @author Nikita Gorodilov
 */
class OtherObjects {

    static Image image() {
        ImageIO.read(new File(RuntimeAgentServiceTest.classLoader.getResource("testimage.jpg").toURI().path))
    }
}
