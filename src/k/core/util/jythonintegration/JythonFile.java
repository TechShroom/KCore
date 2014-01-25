package k.core.util.jythonintegration;

import java.io.File;

import org.python.core.PyObject;
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
     *            -
     * @return
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

    public JythonClass getJClass(String className) {
        return new JythonClass(this, className);
    }
}
