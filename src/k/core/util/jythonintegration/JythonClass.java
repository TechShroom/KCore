package k.core.util.jythonintegration;

import org.python.core.PyObject;
import org.python.core.PyString;

public class JythonClass {
    /**
     * The parent file for this class
     */
    private JythonFile parent = null;
    /**
     * The PyObject associated with this class
     */
    private PyObject us = null;
    /**
     * The class name
     */
    private String us_str;

    /**
     * Wraps around the given class.
     * 
     * @param jfile
     *            - the {@link JythonFile} that the
     * @param className
     */
    public JythonClass(JythonFile jfile, String className) {
        parent = jfile;

        // Prevents errors later
        us = parent.interpreter.get(className);
        us_str = className;
    }

    /**
     * Invokes the given method from this class
     * 
     * @param mName
     *            - the name of the method to invoke
     * @param args
     *            - the arguments to hand to the method
     * @return the result of invoking the method
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

    /**
     * Returns the backing class object
     * 
     * @return the {@link PyObject} that backs this class
     */
    public PyObject getPyClass() {
        return us;
    }

    /**
     * Gets a new instance of this class
     * 
     * @param args
     *            - the arguments (optional) to use in creation
     * @return a new instance
     */
    public PyObject newInstance(PyObject... args) {
        return getPyClass().__call__(args);
    }
}
