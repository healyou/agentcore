package agenttask.evoj

import net.sourceforge.evoj.strategies.sorting.AbstractSimpleRating

/**
 * Created on 22.05.2017 20:38
 * @autor Nikita Gorodilov
 */
class Rating : AbstractSimpleRating<Solution, Double>() {

    public override fun doCalcRating(solution: Solution): Double? {
        val fn = calcFunction(solution)
        if (java.lang.Double.isNaN(fn)) {
            return null
        } else {
            return -fn
        }
    }

    private fun calcFunction(solution: Solution): Double {
        val x = solution.x
        val y = solution.y
        return 12.0 * x * x + 8 * x + 9.0 * y * y
    }
}

/*

public interface PolyPicture {

    @ListParams(length = "50")
    List<Polygon> getPolygons();
}

public interface Polygon {

    Colour getColor();

    @ListParams(length = "8")
    List<Point> getPoints();
}

public interface Point {

    @Range(min = "-5", max = "325", strict = "true")
    float getX();

    @Range(min = "-5", max = "245", strict = "true")
    float getY();

    void setX(float x);

    void setY(float y);
}

public interface Colour {

    @MutationRange("0.2")
    @Range(min = "0", max = "1", strict = "true")
    float getRed();

    @MutationRange("0.2")
    @Range(min = "0", max = "1", strict = "true")
    float getGreen();

    @MutationRange("0.2")
    @Range(min = "0", max = "1", strict = "true")
    float getBlue();

    void setRed(float red);

    void setGreen(float green);

    void setBlue(float blue);

}

public class DemoRating extends AbstractSimpleRating<PolyPicture, Long> {

    private BufferedImage originalImage;

    public DemoRating(BufferedImage pImg) {
        this.originalImage = new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR);
        originalImage.getGraphics().drawImage(pImg, 0, 0, 320, 240, null);
    }

    @Override
    protected Long doCalcRating(PolyPicture picture) {
        BufferedImage img = drawPicture(picture);
        int[] originalPix = getPixels(originalImage);
        int[] testPix = getPixels(img);
        return -calcError(originalPix, testPix);
    }

    private BufferedImage drawPicture(PolyPicture picture) {
        BufferedImage result = new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = result.createGraphics();
        drawPolygons(graphics, picture.getPolygons());
        graphics.dispose();
        return result;
    }

    private void drawPolygons(Graphics2D graphics, List<Polygon> polygons) {
        for (Polygon poly : polygons) {
            List<Point> points = poly.getPoints();
            int x[] = new int[points.size()];
            int y[] = new int[points.size()];
            for (int i = 0; i < points.size(); i++) {
                final Point point = points.get(i);
                x[i] = (int) point.getX();
                y[i] = (int) point.getY();
            }
            Colour clr = poly.getColor();
            graphics.setColor(new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), 0.5f));
            graphics.fillPolygon(x, y, x.length);
        }
    }

    private int[] getPixels(BufferedImage img) {
        return img.getRaster().getPixels(0, 0, img.getWidth(), img.getHeight(), (int[]) null);
    }

    private Long calcError(int[] originalPix, int[] testPix) {
        long result = 0;
        for (int i = 0; i < originalPix.length; i++) {
            int diff = originalPix[i] - testPix[i];
            result += diff * diff;
        }
        return result;
    }
}

DefaultPoolFactory factory = new DefaultPoolFactory();
GenePool<PolyPicture> pool = factory.createPool(40, PolyPicture.class, null);
DemoRating rating = new DemoRating(PICTURE_TO_APPROX);
MultithreadedHandler handler = new MultithreadedHandler(2, rating, null, null, null);

handler.iterate(pool, 1000);

handler.shutdown();

 */