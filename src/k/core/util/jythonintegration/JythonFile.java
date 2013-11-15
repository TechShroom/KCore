package k.core.util.jythonintegration;

import java.io.File;
import java.rmi.UnexpectedException;

import org.python.core.PyMethod;
import org.python.core.PyObject;
import org.python.core.PyType;
import org.python.util.PythonInterpreter;

public class JythonFile {
	private File f = null;

	/**
	 * Holds the private interpreter, if one is needed
	 */
	PythonInterpreter interpreter = null;

	public JythonFile(File pyfile, boolean newInterpreter) {
		f = pyfile.getAbsoluteFile();
		if (newInterpreter) {
			interpreter = new PythonInterpreter();
		} else {
			interpreter = JythonIntergration.mainInterpreter;
		}
		interpreter.execfile(f.getAbsolutePath());
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
		PyObject method = interpreter.get(mName);
		if (method.getType().getName().equals("function")) {
			invokeres = method.__call__();
		} else {
			throw new RuntimeException(new UnexpectedException(
					"Expected function, got " + method.getType().getName()));
		}
		return invokeres;
	}
}
