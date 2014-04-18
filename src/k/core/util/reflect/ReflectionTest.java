package k.core.util.reflect;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;

import k.core.util.reflect.Translate.Language;

public class ReflectionTest {

    @SuppressWarnings("unused")
    private final BigInteger testing_field = new BigInteger("SUCCESS", 36);

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            ReflectionTest testObj = new ReflectionTest();
            System.out.println("first it was "
                    + Reflect.getField(BigInteger.class, "testing_field",
                            testObj));
            Reflect.setField(BigInteger.class, "testing_field", testObj,
                    BigInteger.ONE);
            System.out.println("then it was "
                    + Reflect.getField(BigInteger.class, "testing_field",
                            testObj));
            System.out.println(Reflect.invokeMethod(String.class,
                    "reflect_testm", new ReflectionTest(),
                    "i tested reflection on methods"));
            System.out.println(Reflect.invokeMethodStatic(String.class,
                    Translate.class, "translate", "REFLECTION TEST KEY",
                    Language.JAVA, new Object[] {}));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unused")
    private static String reflect_testm(String input) {
        try {
            return Reflect.invokeMethodStatic(String.class, String.class,
                    "format", input + " and this meant it worked - %s!",
                    new Object[] { "twice" });
        } catch (Exception e) {
            return input + " and this meant it failed! "
                    + e.getClass().getName() + ": " + e.getMessage();
        }
    }

}
