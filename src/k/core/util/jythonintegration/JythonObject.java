package k.core.util.jythonintegration;

import java.util.Arrays;
import java.util.List;

import org.python.core.PyObject;
import org.python.core.PyString;

public class JythonObject {
	private PyObject us = null;

	public JythonObject(PyObject inst) {
		us = inst;
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
		if (args == null || args.length == 0) {
			args = new PyObject[1];
			args[0] = us;
		} else if (args != null) {
			List<PyObject> t = Arrays.asList(args);
			t.add(0, us);
			args = t.toArray(args);
		}
		if (method != null
				&& (method.getType().getName().equals("function") || (args != null
						&& args.length > 0 && method.getType().getName()
						.equals("instancemethod")))) {
			invokeres = method.__call__(args);
		} else {
			throw new RuntimeException("Expected function, got "
					+ (method == null ? "null" : method.getType().getName())
					+ " for " + mName);
		}
		return invokeres;
	}
}
