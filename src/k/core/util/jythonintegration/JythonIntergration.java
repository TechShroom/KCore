package k.core.util.jythonintegration;

import java.io.File;

import org.python.util.PythonInterpreter;

public class JythonIntergration {
    public static PythonInterpreter mainInterpreter = null;

    static {
	mainInterpreter = new PythonInterpreter();
    }

    public static JythonFile getFile(File f) {
	return new JythonFile(f, false);
    }

    public static JythonFile getFile(String f) {
	return new JythonFile(new File(f), false);
    }

    public static JythonFile getFile(File f, boolean newInterp) {
	return new JythonFile(f, newInterp);
    }

    public static JythonFile getFile(String f, boolean newInterp) {
	return new JythonFile(new File(f), newInterp);
    }
}
