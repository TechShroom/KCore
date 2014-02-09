package k.core.util.jythonintegration;

import java.io.File;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class JythonFile {
    /**
     * The {@link File} that backs this JFile.
     */
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
        interpreter.eval("__import__('sys').path.append('"
                + pyfile.getParentFile().getAbsolutePath() + "')");
        interpreter.execfile(f.getAbsolutePath());
    }

    /**
     * Invokes the given method
     * 
     * @param mName
     *            - the name of the method to invoke
     * @param args
     *            - the arguments to pass to the method
     * @return the result of invoking the method
     */
    public PyObject invokeMethod(String mName, PyObject... args) {
        PyObject invokeres = null;
        PyObject method = interpreter.get(mName);
        if (method.getType().getName().equals("function")) {
            invokeres = method.__call__(args);
        } else {
            throw new RuntimeException("Expected function, got "
                    + method.getType().getName());
        }
        return invokeres;
    }

    /**
     * Gets the class by name (like {@link Class#forName(String)}.
     * 
     * @param className
     *            - the name of the class
     * @return a {@link JythonClass} object representing the class
     */
    public JythonClass getJClass(String className) {
        return new JythonClass(this, className);
    }
}
