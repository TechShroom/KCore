package k.core.util.classes;

public class ClassHelp {
    /**
     * Special casting cases for auto-boxed values that don't match
     * {@link Class#isAssignableFrom(Class)}'s return values.
     * 
     * @param a
     *            - the original class
     * @param b
     *            - the new class
     * @return if you can cast an object of class <tt>a</tt> to class <tt>b</tt>
     */
    public static boolean castable(Class<?> a, Class<?> b) {
        return a.isAssignableFrom(b) || (a == int.class && b == Integer.class) // int
                || (a == Integer.class && b == int.class) // int
                || (a == Short.class && b == short.class) // short
                || (a == short.class && b == Short.class) // short
                || (a == Byte.class && b == byte.class) // byte
                || (a == byte.class && b == Byte.class) // byte
                || (a == Long.class && b == long.class) // long
                || (a == long.class && b == Long.class) // long
                || (a == Float.class && b == float.class) // float
                || (a == float.class && b == Float.class) // float
                || (a == Double.class && b == double.class) // double
                || (a == double.class && b == Double.class) // double
                || (a == Character.class && b == char.class) // char
                || (a == char.class && b == Character.class) // char
                || (a == Boolean.class && b == boolean.class) // boolean
                || (a == boolean.class && b == Boolean.class); // boolean
    }

    /**
     * Returns the class values for every argument, so that
     * {@link Class#getDeclaredConstructor(Class...)} or
     * {@link Class#getDeclaredMethod(String, Class...)} can be used with ease.
     * 
     * @param objects
     *            - the original object array
     * @return matching class values. If a value is null, it will be of the
     *         passed in array's component type.
     */
    public static Class<?>[] classesFromObjects(Object[] objects) {
        if (objects == null) {
            return new Class<?>[0];
        }
        Class<?>[] out = new Class<?>[objects.length];
        int index = 0;
        for (Object o : objects) {
            if (o == null) {
                out[index] = objects.getClass().getComponentType();
            } else {
                out[index] = o.getClass();
            }
            index++;
        }
        return out;
    }

    /**
     * Gets the associated high level class that type is auto-boxed to, or
     * <tt>null</tt> if <tt>type</tt> is not auto-boxed
     * 
     * @param type
     *            - the type to get a value for
     * @return the matching high level class, or <tt>null</tt> if there is none
     */
    public static Class<?> getPrimitveClassAutoboxed(Class<?> type) {
        Class<?> out = null;
        if (type == int.class) {
            out = Integer.class;
        } else if (type == short.class) {
            out = Short.class;
        } else if (type == byte.class) {
            out = Byte.class;
        } else if (type == long.class) {
            out = Long.class;
        } else if (type == float.class) {
            out = Float.class;
        } else if (type == boolean.class) {
            out = Boolean.class;
        } else if (type == double.class) {
            out = Double.class;
        } else if (type == char.class) {
            out = Character.class;
        }
        return out;
    }
}
