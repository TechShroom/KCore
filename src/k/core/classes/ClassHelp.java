package k.core.classes;

public class ClassHelp {
	public static boolean castable(Class<?> a, Class<?> b) {
		return a.isAssignableFrom(b) || (a == int.class && b == Integer.class)
				|| (a == Integer.class && b == int.class);
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
