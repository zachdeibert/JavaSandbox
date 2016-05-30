package com.github.zachdeibert.javasandbox;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

final class Factory {
	private static final DocumentBuilderFactory xmlBldrFactory;
	private static final DocumentBuilder xmlBldr;

	static {
		try {
			xmlBldrFactory = DocumentBuilderFactory.newInstance();
			xmlBldr = xmlBldrFactory.newDocumentBuilder();
		} catch ( final ParserConfigurationException ex ) {
			throw new RuntimeException(ex);
		}
	}

	static final Document loadXMLDocument(final InputStream in) throws SAXException, IOException {
		return xmlBldr.parse(in);
	}
}
