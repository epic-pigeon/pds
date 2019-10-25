import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Locale;

public class Test {
    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        System.out.println(Thread.currentThread().getContextClassLoader().loadClass("java.lang.String"));
    }
}
