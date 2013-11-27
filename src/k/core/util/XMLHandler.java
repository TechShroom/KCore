package k.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XMLHandler {
	public static Document loadDocument(File dom) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(dom);
		doc.normalizeDocument();
		return doc;
	}

	public static void printXMLNode(Node node, int level) {
		// Indent for pretty print
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
					// Removes extra #text whitespace
					nodeVal = nodeVal.replaceFirst("\\s+$", "");
				}
				System.err
						.println(String
								.format(tab
										+ "Node #%s: name: '%s'; value: '%s'; childNodes: [",
										i, node.getChildNodes().item(i)
												.getNodeName(), nodeVal));
				printXMLNode(node.getChildNodes().item(i), level + 1);
				System.err.println(tab + "]");
			}
		}
	}

	public static boolean hasAttr(Node n, String s) {
		NamedNodeMap attrs = n.getAttributes();
		return attrs.getNamedItem(s) != null;
	}

	public static String getAttr(Node n, String s) {
		return n.getAttributes().getNamedItem(s).getNodeValue();
	}

	public static int getAttrInt(Node n, String s) {
		return Integer.parseInt(getAttr(n, s));
	}

	public static boolean getAttrBool(Node n, String s) {
		return Boolean.parseBoolean(getAttr(n, s));
	}

	public static float getAttrFloat(Node n, String s) {
		return Float.parseFloat(getAttr(n, s));
	}

	public static double getAttrDouble(Node n, String s) {
		return Double.parseDouble(getAttr(n, s));
	}

	public static Node getNodeByName(Node n, String s) {
		Node out = null;
		for (int i = 0; i < n.getChildNodes().getLength(); i++) {
			Node t = n.getChildNodes().item(i);
			if (t.getNodeName().equals(s)) {
				out = t;
				break;
			}
		}
		return out;
	}

	public static List<Node> getNodesByName(Node n, String s) {
		List<Node> out = new ArrayList<Node>();
		for (int i = 0; i < n.getChildNodes().getLength(); i++) {
			Node t = n.getChildNodes().item(i);
			if (t.getNodeName().equals(s)) {
				out.add(t);
			}
		}
		return out;
	}
}
