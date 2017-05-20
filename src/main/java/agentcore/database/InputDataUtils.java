package agentcore.database;

/**
 * Created on 17.02.2017 17:18
 *
 * @autor Nikita Gorodilov
 */
public class InputDataUtils {

    public static <T> boolean equals(T a, T b) {
        return a == null ? b == null : a.equals(b);
    }

    public static <T> T nvl(T obj, T ifNull) {
        return obj != null ? obj : ifNull;
    }

}
