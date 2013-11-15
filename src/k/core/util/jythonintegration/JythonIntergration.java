package k.core.util.jythonintegration;

import org.python.util.PythonInterpreter;

public class JythonIntergration {
	public static PythonInterpreter mainInterpreter = null;
	
	static {
		mainInterpreter = new PythonInterpreter();
	}
}
