package k.core.test;

import java.io.File;
import java.net.URISyntaxException;

import k.core.util.Helper;
import k.core.util.jythonintegration.JythonClass;
import k.core.util.jythonintegration.JythonFile;

import org.python.core.PyObject;

public class JythonIntegrateTest {
	public static void main(String[] args) throws URISyntaxException {
		System.err.println("rel-path="
				+ Helper.Files.getFileRelativeToTopLevel(""));
		File jython = Helper.Files.getFileRelativeToTopLevel("jythontest.py");
		JythonFile file = new JythonFile(jython, false);
		file.invokeMethod("main");
		JythonClass cls = new JythonClass(file, "TestClass");
		System.err.println("Loaded class as obj");
		PyObject instance = cls.getPyClass().__call__();
		cls.invokeMethod("notInstMethod", instance);
		System.err.println("Proccessed test file successfully");
	}
}
