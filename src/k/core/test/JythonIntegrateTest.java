package k.core.test;

import java.io.File;
import java.net.URISyntaxException;

import org.python.core.PyObject;

import k.core.util.jythonintegration.JythonClass;
import k.core.util.jythonintegration.JythonFile;

public class JythonIntegrateTest {
	public static void main(String[] args) throws URISyntaxException {
		File jython = new File(new File(JythonIntegrateTest.class
				.getResource("JythonIntegrateTest.class").toURI().getPath())
				.getParentFile().getParentFile().getParentFile()
				.getParentFile().getParentFile(), "jythontest.py");
		JythonFile file = new JythonFile(jython, false);
		file.invokeMethod("main");
		JythonClass cls = new JythonClass(file, "TestClass");
		System.err.println("Loaded class as obj");
		PyObject instance = cls.getPyClass().__call__();
		cls.invokeMethod("notInstMethod", instance);
		System.err.println("Proccessed test file successfully");
	}
}
