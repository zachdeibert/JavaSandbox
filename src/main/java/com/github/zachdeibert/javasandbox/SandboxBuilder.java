package com.github.zachdeibert.javasandbox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SandboxBuilder {
	private final List<ClassLoader> loaders;
	private final List<URL> urls;
	private JvmSubsetBuilder subset;

	public Sandbox build() {
		if ( !urls.isEmpty() ) {
			loaders.add(new URLClassLoader(urls.toArray(new URL[0])));
			urls.clear();
		}
		return new Sandbox(subset.build(), loaders.toArray(new ClassLoader[0]));
	}

	public SandboxBuilder addClassLoaders(final ClassLoader... loaders) {
		this.loaders.addAll(Arrays.asList(loaders));
		return this;
	}

	public SandboxBuilder addURLs(final URL... urls) {
		this.urls.addAll(Arrays.asList(urls));
		return this;
	}

	public SandboxBuilder loadSubset(final InputStream in) throws IOException {
		subset = subset.load(in);
		return this;
	}

	public SandboxBuilder loadSubset(final URL url) throws IOException {
		subset = subset.load(url);
		return this;
	}

	public SandboxBuilder loadSubset(final String res) throws IOException {
		subset = subset.load(res);
		return this;
	}

	public SandboxBuilder loadSubset(final File file) throws IOException {
		subset = subset.load(file);
		return this;
	}

	public SandboxBuilder setDef(final boolean def) {
		subset = subset.setDef(def);
		return this;
	}

	public SandboxBuilder addRules(final JvmSubsetRule... rules) {
		subset = subset.addRules(rules);
		return this;
	}

	public SandboxBuilder toInlcudes() {
		subset = subset.toInlcudes();
		return this;
	}

	public SandboxBuilder toRules() {
		subset = subset.toRules();
		return this;
	}

	public SandboxBuilder() {
		loaders = new ArrayList<ClassLoader>();
		urls = new ArrayList<URL>();
		subset = new JvmSubsetBuilder();
	}
}
