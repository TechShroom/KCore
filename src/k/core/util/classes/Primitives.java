package k.core.util.classes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Primitives {
    /**
     * The number of primitive types in Java.
     */
    public static final int PRIMITIVE_COUNT = 7;
    /**
     * PTW and WTP mappings. Read-only.
     */
    private static final Map<Class<?>, Class<?>> primitivesToWrapper,
            wrapperToPrimitives;

    /**
     * Arrays of classes.
     */
    private static final Class<?>[] primitive = { byte.class, short.class,
            int.class, long.class, double.class, char.class, boolean.class },
            wrapper = { Byte.class, Short.class, Integer.class, Long.class,
                    Double.class, Character.class, Boolean.class };

    private static final List<Class<?>> all;

    private static final Method[] valueOfs = new Method[PRIMITIVE_COUNT];

    /**
     * Constants for indexes in arrays.
     */
    private static final int BYTE = 0, SHORT = 1, INT = 2, LONG = 3,
            DOUBLE = 4, CHAR = 5, BOOL = 6;
    static {
        HashMap<Class<?>, Class<?>> ptw = new HashMap<Class<?>, Class<?>>(), wtp = new HashMap<Class<?>, Class<?>>();
        for (int i = 0; i < PRIMITIVE_COUNT; i++) {
            Class<?> wrpc = wrapper[i];
            ptw.put(primitive[i], wrpc);
            wtp.put(wrpc, primitive[i]);
            try {
                if (wrpc == Character.class) {
                    valueOfs[i] = Primitives.class.getDeclaredMethod(
                            "overriden_vo_char", String.class);
                    continue;
                }
                valueOfs[i] = wrpc.getDeclaredMethod("valueOf", String.class);
            } catch (Exception e) {
                System.err.println("Error getting valueOf for " + wrpc);
                System.exit(-1);
            }
        }
        // Doing this here prevents us having to do it over and over when
        // requested.
        primitivesToWrapper = Collections
                .<Class<?>, Class<?>> unmodifiableMap(ptw);
        wrapperToPrimitives = Collections
                .<Class<?>, Class<?>> unmodifiableMap(wtp);
        List<Class<?>> all0 = new ArrayList<Class<?>>(PRIMITIVE_COUNT * 2);
        all0.addAll(Arrays.asList(primitive));
        all0.addAll(Arrays.asList(wrapper));
        all = Collections.unmodifiableList(all0);
    }

    public static Map<Class<?>, Class<?>> getPTWMap() {
        return primitivesToWrapper;
    }

    public static Map<Class<?>, Class<?>> getWTPMap() {
        return wrapperToPrimitives;
    }

    public static Class<?>[] getPArray() {
        return primitive.clone();
    }

    public static Class<?>[] getWArray() {
        return wrapper.clone();
    }

    public static boolean isByte(Class<?> c) {
        return c == primitive[BYTE] || c == wrapper[BYTE];
    }

    public static boolean isShort(Class<?> c) {
        return c == primitive[SHORT] || c == wrapper[SHORT];
    }

    public static boolean isInt(Class<?> c) {
        return c == primitive[INT] || c == wrapper[INT];
    }

    public static boolean isLong(Class<?> c) {
        return c == primitive[LONG] || c == wrapper[LONG];
    }

    public static boolean isDouble(Class<?> c) {
        return c == primitive[DOUBLE] || c == wrapper[DOUBLE];
    }

    public static boolean isChar(Class<?> c) {
        return c == primitive[CHAR] || c == wrapper[CHAR];
    }

    public static boolean isBool(Class<?> c) {
        return c == primitive[BOOL] || c == wrapper[BOOL];
    }

    /**
     * Gets the class that wraps this primitive class, if there is one.
     * 
     * @param c
     *            - a primitive or non-primitive class
     * @return <tt>c</tt> if it is not primitive, otherwise the wrapper class.
     */
    public static Class<?> getWrapperClass(Class<?> c) {
        Class<?> possible = primitivesToWrapper.get(c);
        return (possible == null) ? c : possible;
    }

    /**
     * Returns the index used for the given class
     * 
     * @param c
     *            - a class, preferably one that represents one of the primitive
     *            or wrapper classes.
     * @return the appropriate index for the {@link Primitives#getWArray()
     *         wrapper} or {@link Primitives#getPArray() primitive} arrays, or
     *         -1 if <tt>c</tt> does not represent one of appropriate classes.
     */
    public static int indexForClass(Class<?> c) {
        if (isByte(c)) {
            return BYTE;
        }
        if (isShort(c)) {
            return SHORT;
        }
        if (isInt(c)) {
            return INT;
        }
        if (isLong(c)) {
            return LONG;
        }
        if (isDouble(c)) {
            return DOUBLE;
        }
        if (isChar(c)) {
            return CHAR;
        }
        if (isBool(c)) {
            return BOOL;
        }
        return -1;
    }

    /**
     * Attempts to parse the string.
     * 
     * @param <T>
     *            - the type to return
     * @param s
     *            - the string to parse
     * @return the string converted into the desired format, or null if
     *         reflection fails.
     * @throws ClassCastException
     *             if the class is not a primitive or wrapper class.
     */
    @SuppressWarnings("unchecked")
    public static <T> T parse(Class<T> format, String s)
            throws ClassCastException {
        if (!all.contains(format)) {
            throw new ClassCastException(
                    "cannot preform primitive parse on non-primitve " + format);
        }
        try {
            return (T) valueOfs[indexForClass(format)].invoke(null, s);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    private static Character overriden_vo_char(String s) {
        return s.charAt(0);
    }

    private Primitives() {
    }
}
