package com.github.zachdeibert.javasandbox;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

public class FactoryTest {
	@Test
	public void testLoadXMLDocument() throws SAXException, IOException {
		Assert.assertNotNull(Factory.loadXMLDocument(
				ClassLoader.getSystemResourceAsStream("com/github/zachdeibert/javasandbox/simple.xml")));
	}
}
