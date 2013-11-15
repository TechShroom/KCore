package k.core.util.jythonintegration;

import java.io.File;

import org.python.core.PyMethod;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class JythonFile {
	private File f = null;
	
	/**
	 * Holds the private interpreter, if one is needed
	 */
	private PythonInterpreter interpreter = null;
	
	public JythonFile(File pyfile, boolean newInterpreter) {
		f = pyfile.getAbsoluteFile();
		if(newInterpreter) {
			interpreter = new PythonInterpreter();
		} else {
			interpreter = JythonIntergration.mainInterpreter;
		}
		interpreter.execfile(f.getAbsolutePath());
	}
	
	/**
	 * Invokes the given method
	 * @param mName - the name of the moethod to invoke
	 * @param args - 
	 * @return
	 */
	public PyObject invokeMethod(String mName, PyObject... args) {
		PyObject invokeres = null;
		PyObject method = interpreter.get(mName);
		invokeres = method.__call__();
		return invokeres;
	}
}
