package k.core.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XMLHandler {
	public static Document loadDocument(File dom) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(dom);
		doc.normalizeDocument();
		return doc;
	}

	public static void printXMLNode(Node node, int level) {
		String tab = "   ";
		for (int i = 0; i < level; i++) {
			tab += tab;
		}
		if (node.getAttributes() != null) {
			for (int i = 0; i < node.getAttributes().getLength(); i++) {
				String nodeVal = node.getAttributes().item(i).getNodeValue();
				if (nodeVal != null) {
					nodeVal = nodeVal.replaceAll("\\s+$", "");
				}
				System.err.println(String.format(tab
						+ "Attr #%s: name: '%s'; value: '%s'", i, node
						.getAttributes().item(i).getNodeName(), nodeVal));
			}
		}
		if (node.getChildNodes() != null) {
			for (int i = 0; i < node.getChildNodes().getLength(); i++) {
				String nodeVal = node.getChildNodes().item(i).getNodeValue();
				if (nodeVal != null) {
					nodeVal = nodeVal.replaceFirst("\\s+$", "");
				}
				System.err.println(String.format(tab
						+ "Node #%s: name: '%s'; value: '%s'; childNodes:", i,
						node.getChildNodes().item(i).getNodeName(), nodeVal));
				printXMLNode(node.getChildNodes().item(i), level + 1);
			}
		}
	}
}
