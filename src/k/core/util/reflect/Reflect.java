package k.core.util.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import k.core.util.classes.ClassHelp;

public class Reflect {
    private static Field fieldModifiers = null;
    static {
        try {
            fieldModifiers = (java.lang.reflect.Field.class)
                    .getDeclaredField("modifiers");
            fieldModifiers.setAccessible(true);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldStatic(Class<T> type, Class<?> from, String name)
            throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        Field f = from.getDeclaredField(name);
        f.setAccessible(true);
        Object test = f.get(null);
        if (!type.isInstance(test)) {
            throw new ClassCastException(f.getType() + " cannot be cast to "
                    + type + " (if you think this is wrong, please report it!)");
        }
        return (T) test;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Class<T> type, String name, Object inst)
            throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        Class<?> from = inst.getClass();
        Field f = from.getDeclaredField(name);
        f.setAccessible(true);
        if (!f.getType().isAssignableFrom(type)
                && !ClassHelp.castable(f.getType(), type)) {
            throw new ClassCastException(f.getType() + " cannot be cast to "
                    + type + " (if you think this is wrong, please report it!)");
        }
        return (T) f.get(inst);
    }

    public static <T> void setFieldStatic(Class<T> type, Class<?> from,
            String name, T value) throws NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {
        Field f = from.getDeclaredField(name);
        int fieldFieldModifiers = fieldModifiers.getInt(f);

        if ((fieldFieldModifiers & Modifier.FINAL) != 0) {
            fieldModifiers.setInt(f, fieldFieldModifiers & ~Modifier.FINAL);
            System.err
                    .println("Removed FINAL, but is static anyways so might not work.");
        }
        f.setAccessible(true);
        if (!f.getType().isAssignableFrom(type)
                && !ClassHelp.castable(f.getType(), type)) {
            throw new ClassCastException(f.getType() + " cannot be cast to "
                    + type + " (if you think this is wrong, please report it!)");
        }
        value = type.cast(value);
        f.set(null, value);
    }

    public static <T> void setField(Class<T> type, String name, Object inst,
            T value) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        Class<?> from = inst.getClass();
        Field f = from.getDeclaredField(name);
        f.setAccessible(true);
        int fieldFieldModifiers = fieldModifiers.getInt(f);

        if ((fieldFieldModifiers & Modifier.FINAL) != 0) {
            fieldModifiers.setInt(f, fieldFieldModifiers & ~Modifier.FINAL);
            System.err.println("Removed FINAL");
        }
        if (!type.isAssignableFrom(f.getType())
                && !ClassHelp.castable(f.getType(), type)) {
            throw new ClassCastException(f.getType() + " cannot be cast to "
                    + type + " (if you think this is wrong, please report it!)");
        }
        value = type.cast(value);
        f.set(inst, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethodStatic(Class<T> retType, Class<?> from,
            String name, Object... objects) throws SecurityException,
            IllegalArgumentException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException {
        Method m = from.getDeclaredMethod(name,
                ClassHelp.classesFromObjects(objects));
        m.setAccessible(true);
        if (!m.getReturnType().isAssignableFrom(retType)
                && !ClassHelp.castable(m.getReturnType(), retType)) {
            throw new ClassCastException(m.getReturnType()
                    + " cannot be cast to " + retType
                    + " (if you think this is wrong, please report it!)");
        }
        return (T) m.invoke(null, objects);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Class<T> retType, String name,
            Object inst, Object... objects) throws SecurityException,
            IllegalArgumentException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException {
        Class<?> from = inst.getClass();
        Method m = from.getDeclaredMethod(name,
                ClassHelp.classesFromObjects(objects));
        m.setAccessible(true);
        if (!m.getReturnType().isAssignableFrom(retType)
                && !ClassHelp.castable(m.getReturnType(), retType)) {
            throw new ClassCastException(m.getReturnType()
                    + " cannot be cast to " + retType
                    + " (if you think this is wrong, please report it!)");
        }
        return (T) m.invoke(inst, objects);
    }

    public static <T> T construct(Class<T> from, Object... args)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        Constructor<T> constr = from.getDeclaredConstructor(ClassHelp
                .classesFromObjects(args));
        constr.setAccessible(true);
        return constr.newInstance(args);
    }
}
