package org.aktin.broker.i2b2.admin;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMUtils {
	public static void stripWhitespace(Element node) throws XPathExpressionException{
		XPathFactory xf = XPathFactory.newInstance();
		// XPath to find empty text nodes.
		XPathExpression xe = xf.newXPath().compile("//text()[normalize-space(.) = '']");  
		NodeList nl = (NodeList)xe.evaluate(node, XPathConstants.NODESET);

		// Remove each empty text node from document.
		for (int i = 0; i < nl.getLength(); i++) {
		    Node empty = nl.item(i);
		    empty.getParentNode().removeChild(empty);
		}
	}
	public static void printDOM(Node node, OutputStream out){
		try {
			printDOM(node, out, "UTF-8");
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}
	public static void printDOM(Node node, OutputStream out, String encoding) throws TransformerException{
		    TransformerFactory tf = TransformerFactory.newInstance();
		    Transformer transformer;
				transformer = tf.newTransformer();
		    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	
		    transformer.transform(new DOMSource(node), 
		         new StreamResult(out));
	}

	public static String toString(Node node) throws TransformerException{
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer;
			transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    transformer.transform(new DOMSource(node), 
	         new StreamResult(out));
	    return Charset.forName("UTF-8").decode(ByteBuffer.wrap(out.toByteArray())).toString();
	}
}
