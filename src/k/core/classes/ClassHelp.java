package k.core.classes;

public class ClassHelp {
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
}
