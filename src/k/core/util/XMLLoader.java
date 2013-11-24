package k.core.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class XMLLoader {
	public static Document loadDocument(File dom) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(dom);
		doc.normalizeDocument();
		return doc;
	}

}
