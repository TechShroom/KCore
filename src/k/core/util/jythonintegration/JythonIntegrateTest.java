package k.core.util.jythonintegration;

import java.io.File;
import java.net.URISyntaxException;

import k.core.util.core.Helper;

import org.python.core.PyObject;

public class JythonIntegrateTest {
    public static void main(String[] args) throws URISyntaxException {
        File jython = Helper.Files.getFileRelativeToTopLevel("jythontest.py");
        JythonFile file = new JythonFile(jython, false);
        file.invokeMethod("main");
        JythonClass cls = new JythonClass(file, "TestClass");
        System.err.println("Loaded class as obj");
        PyObject instance = cls.newInstance();
        JythonObject inst_j = new JythonObject(instance);
        inst_j.invokeMethod("notInstMethod");
        System.err.println("Proccessed test file successfully");
    }
}
