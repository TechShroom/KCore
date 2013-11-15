package k.core.util.jythonintegration;

import java.rmi.UnexpectedException;

import org.python.core.PyObject;

public class JythonClass {
	private JythonFile parent = null;
	private PyObject us = null;

	public JythonClass(JythonFile jfile, String className) {
		parent = jfile;
		us = parent.interpreter.get(className);
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
		PyObject method = parent.interpreter.get(mName);
		if (method.getType().getName().equals("function")) {
			invokeres = method.__call__();
		} else {
			throw new RuntimeException(new UnexpectedException(
					"Expected function, got " + method.getType().getName()));
		}
		return invokeres;
	}
}
