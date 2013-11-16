package k.core.util.jythonintegration;

import org.python.core.PyClass;
import org.python.core.PyMethod;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyType;

public class JythonClass {
	private JythonFile parent = null;
	private PyObject us = null;
	private String us_str;

	public JythonClass(JythonFile jfile, String className) {
		parent = jfile;

		// Prevents errors later
		us = parent.interpreter.get(className);
		us_str = className;
	}

	/**
	 * Invokes the given method
	 * 
	 * @param mName
	 *            - the name of the method to invoke
	 * @param args
	 *            -
	 * @return
	 */
	public PyObject invokeMethod(String mName, PyObject... args) {
		PyObject invokeres = null;
		PyObject method = us.__getattr__(new PyString(mName));
		if (method != null
				&& (method.getType().getName().equals("function") || (args != null
						&& args.length > 0 && method.getType().getName()
						.equals("instancemethod")))) {
			invokeres = method.__call__(args);
		} else {
			throw new RuntimeException("Expected function, got "
					+ (method == null ? "null" : method.getType().getName())
					+ " for " + us_str + "." + mName);
		}
		return invokeres;
	}

	public PyObject getPyClass() {
		return us;
	}
}
