package k.core.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import k.core.classes.ClassHelp;

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
		if (!f.getType().isAssignableFrom(type)
				&& !ClassHelp.castable(f.getType(), type)) {
			throw new ClassCastException(f.getType() + " cannot be cast to "
					+ type);
		}
		return (T) f.get(null);
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
					+ type);
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
			System.err.println("Removed FINAL");
		}
		f.setAccessible(true);
		if (!f.getType().isAssignableFrom(type)
				&& !ClassHelp.castable(f.getType(), type)) {
			throw new ClassCastException(f.getType() + " cannot be cast to "
					+ type);
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
		if (!f.getType().isAssignableFrom(type)
				&& !ClassHelp.castable(f.getType(), type)) {
			throw new ClassCastException(f.getType() + " cannot be cast to "
					+ type);
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
					+ " cannot be cast to " + retType);
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
					+ " cannot be cast to " + retType);
		}
		return (T) m.invoke(inst, objects);
	}

}
