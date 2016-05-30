package com.github.zachdeibert.javasandbox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JvmSubsetBuilder {
	private boolean def;
	private final List<JvmSubsetRule> rules;
	private final JvmSubsetBuilder includes;
	private final JvmSubsetBuilder parent;

	protected JvmSubsetRule parseRule(final Element node) throws IOException {
		boolean allow = true;
		if ( node.hasAttribute("allow") ) {
			allow = Boolean.parseBoolean(node.getAttribute("allow"));
		}
		return new JvmSubsetRule(allow, node.getTextContent());
	}

	protected void parseElement(final Element node) throws IOException {
		switch ( node.getTagName() ) {
		case "rule":
			rules.add(parseRule(node));
			break;
		case "include":
			includes.rules.add(parseRule(node));
			break;
		default:
			throw new IOException("Invalid tag name");
		}
	}

	protected void load(final Element node) throws IOException {
		if ( node.hasAttribute("default") ) {
			def = Boolean.parseBoolean(node.getAttribute("default"));
		}
		if ( node.hasAttribute("includeDefault") ) {
			includes.def = Boolean.parseBoolean(node.getAttribute("includeDefault"));
		}
		NodeList list = node.getChildNodes();
		for ( int i = 0; i < list.getLength(); ++i ) {
			Node sub = list.item(i);
			if ( sub instanceof Element ) {
				parseElement((Element) sub);
			}
		}
	}

	public JvmSubsetBuilder load(final InputStream in) throws IOException {
		try {
			Document xml = Factory.loadXMLDocument(in);
			toRules().load(xml.getDocumentElement());
			return this;
		} catch ( SAXException ex ) {
			throw new IOException(ex);
		}
	}

	public JvmSubsetBuilder load(final URL url) throws IOException {
		try ( InputStream in = url.openStream() ) {
			return load(in);
		}
	}

	public JvmSubsetBuilder load(final String res) throws IOException {
		return load(ClassLoader.getSystemResource(res));
	}

	public JvmSubsetBuilder load(final File file) throws IOException {
		try {
			return load(file.toURI().toURL());
		} catch ( final MalformedURLException ex ) {
			throw new IOException(ex);
		}
	}

	public JvmSubsetBuilder setDef(final boolean def) {
		this.def = def;
		return this;
	}

	public JvmSubsetBuilder addRules(final JvmSubsetRule... rules) {
		this.rules.addAll(Arrays.asList(rules));
		return this;
	}

	public JvmSubsetBuilder toInlcudes() {
		if ( includes == null ) {
			return this;
		} else {
			return includes;
		}
	}

	public JvmSubsetBuilder toRules() {
		if ( parent == null ) {
			return this;
		} else {
			return parent;
		}
	}

	private JvmSubset buildThis() {
		return new JvmSubset(def, includes == null ? null : includes.buildThis(), rules.toArray(new JvmSubsetRule[0]));
	}

	public JvmSubset build() {
		return toRules().buildThis();
	}

	private JvmSubsetBuilder(final JvmSubsetBuilder parent) {
		def = parent != null;
		rules = new ArrayList<JvmSubsetRule>();
		if ( parent == null ) {
			includes = new JvmSubsetBuilder(this);
		} else {
			includes = null;
		}
		this.parent = parent;
	}

	public JvmSubsetBuilder() {
		this(null);
	}
}
