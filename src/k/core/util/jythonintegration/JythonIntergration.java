package k.core.util.jythonintegration;

import java.io.File;

import org.python.util.PythonInterpreter;

public class JythonIntergration {
    /**
     * The common interpreter used by default
     */
    public static final PythonInterpreter mainInterpreter;

    static {
        // sets up the interpreter
        mainInterpreter = new PythonInterpreter();
    }

    /**
     * Gets a new {@link JythonFile} using the common interpreter
     * 
     * @param f
     *            - the file location
     * @return a JythonFile representing the given Python file
     */
    public static JythonFile getFile(File f) {
        return new JythonFile(f, false);
    }

    /**
     * Gets a new {@link JythonFile} using the common interpreter
     * 
     * @param f
     *            - the file location
     * @return a JythonFile representing the given Python file
     */
    public static JythonFile getFile(String f) {
        return new JythonFile(new File(f), false);
    }

    /**
     * Gets a new {@link JythonFile}, optionally with a new interpreter.
     * 
     * @param f
     *            - the file location
     * @param newInterp
     *            - if a new interpreter should be generated for this file
     * @return a JythonFile representing the given Python file
     */
    public static JythonFile getFile(File f, boolean newInterp) {
        return new JythonFile(f, newInterp);
    }

    /**
     * Gets a new {@link JythonFile}, optionally with a new interpreter.
     * 
     * @param f
     *            - the file location
     * @param newInterp
     *            - if a new interpreter should be generated for this file
     * @return a JythonFile representing the given Python file
     */
    public static JythonFile getFile(String f, boolean newInterp) {
        return new JythonFile(new File(f), newInterp);
    }
}
