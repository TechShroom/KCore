package k.core.test;

import java.io.File;
import java.net.URISyntaxException;

import k.core.util.jythonintegration.JythonFile;

public class JythonIntegrateTest {
	public static void main(String[] args) throws URISyntaxException {
		File jython = new File(new File(JythonIntegrateTest.class
				.getResource("JythonIntegrateTest.class").toURI().getPath())
				.getParentFile().getParentFile().getParentFile()
				.getParentFile().getParentFile(), "jythontest.py");
		JythonFile file = new JythonFile(jython, false);
		file.invokeMethod("main");
		System.err.println("Proccessed test file successfully");
	}
}
