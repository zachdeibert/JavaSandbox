package com.github.zachdeibert.javasandbox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

final class SandboxClassLoader extends ClassLoader {
	private final Sandbox sandbox;
	private final ClassLoader[] parents;

	@Override
	protected final Class<?> findClass(final String name) throws ClassNotFoundException {
		try {
			ClassWriter cw = new ClassWriter(0);
			ClassVisitor cv = new SandboxTransformer(cw, sandbox);
			ClassReader cr = new ClassReader(getResourceAsStream(name.replace('.', '/').concat(".class")));
			cr.accept(cv, 0);
			byte[] b = cw.toByteArray();
			return defineClass(name, b, 0, b.length);
		} catch ( IOException | IllegalReferenceException ex ) {
			throw new ClassNotFoundException("Unable to load class", ex);
		}
	}

	@Override
	protected final URL findResource(final String name) {
		if ( name.startsWith(String.format("com/github/zachdeibert/javasandbox/vm%s/", sandbox.vm)) ) {
			return findResource(name.substring(54));
		}
		for ( final ClassLoader parent : parents ) {
			URL url = parent.getResource(name);
			if ( url != null ) {
				return url;
			}
		}
		return null;
	}

	@Override
	protected final Enumeration<URL> findResources(final String name) throws IOException {
		if ( name.startsWith(String.format("com/github/zachdeibert/javasandbox/vm%s/", sandbox.vm)) ) {
			return findResources(name.substring(54));
		}
		final List<URL> urls = new ArrayList<URL>();
		for ( final ClassLoader parent : parents ) {
			final Enumeration<URL> e = parent.getResources(name);
			while ( e.hasMoreElements() ) {
				urls.add(e.nextElement());
			}
		}
		return new Enumeration<URL>() {
			private Iterator<URL> it = urls.iterator();

			@Override
			public final boolean hasMoreElements() {
				return it.hasNext();
			}

			@Override
			public final URL nextElement() {
				return it.next();
			}
		};
	}

	SandboxClassLoader(final Sandbox sandbox, final ClassLoader[] parents) {
		this.sandbox = sandbox;
		this.parents = parents;
	}
}
