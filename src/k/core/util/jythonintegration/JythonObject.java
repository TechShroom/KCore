package k.core.util.jythonintegration;

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
     *            - the arguments to pass to the method
     * @return the result of invoking the method
     */
    public PyObject invokeMethod(String mName, PyObject... args) {
        PyObject invokeres = null;
        PyObject method = us.__getattr__(new PyString(mName));
        if (method != null
                && (method.getType().getName().equals("function") || method
                        .getType().getName().equals("instancemethod"))) {
            invokeres = method.__call__(args);
        } else {
            throw new RuntimeException("Expected function, got "
                    + (method == null ? "null" : method.getType().getName())
                    + " for " + mName);
        }
        return invokeres;
    }
}
