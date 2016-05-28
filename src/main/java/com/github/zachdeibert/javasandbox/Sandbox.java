package com.github.zachdeibert.javasandbox;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Random;

public final class Sandbox {
	private final SandboxClassLoader cl;
	final JvmSubset subset;
	final String vm;

	public final SandboxedObject constructObject(final String cls, final boolean elevate, final Object... args)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException,
			InstantiationException, InvocationTargetException {
		final Class<?> c = cl.loadClass(String.format("com.github.zachdeibert.javasandbox.vm%s.%s", vm, cls));
		final Class<?>[] argTypes = new Class<?>[args.length];
		for ( int i = 0; i < args.length; ++i ) {
			argTypes[i] = args[i].getClass();
		}
		final Constructor<?> ctor = c.getConstructor(argTypes);
		if ( !ctor.isAccessible() ) {
			if ( elevate ) {
				ctor.setAccessible(true);
			} else {
				throw new IllegalAccessException("Constructor is not accessible");
			}
		}
		return new SandboxedObject(ctor.newInstance(args));
	}

	public final SandboxedObject constructObject(final String cls, final Object... args)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException,
			InstantiationException, InvocationTargetException {
		return constructObject(cls, false, args);
	}

	public final Object callStaticMethod(final String cls, final String method, final boolean elevate,
			final Object... args)
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		final Class<?> c = cl.loadClass(String.format("com.github.zachdeibert.javasandbox.vm%s.%s", vm, cls));
		final Class<?>[] argTypes = new Class<?>[args.length];
		for ( int i = 0; i < args.length; ++i ) {
			argTypes[i] = args[i].getClass();
		}
		final Method m = c.getDeclaredMethod(method, argTypes);
		if ( !m.isAccessible() ) {
			if ( elevate ) {
				m.setAccessible(true);
			} else {
				throw new IllegalAccessException("Method is not accessible");
			}
		}
		if ( !Modifier.isStatic(m.getModifiers()) ) {
			throw new IllegalArgumentException("Method is not static");
		}
		return m.invoke(null, args);
	}

	public final Object callStaticMethod(final String cls, final String method, final Object... args)
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		return callStaticMethod(cls, method, false, args);
	}

	public Sandbox(final JvmSubset subset, final ClassLoader... classLoaders) {
		this.subset = subset;
		final char[] vm = new char[16];
		final Random rand = new Random();
		for ( int i = 0; i < vm.length; ++i ) {
			vm[i] = Integer.toHexString(rand.nextInt(16)).charAt(0);
		}
		this.vm = new String(vm);
		cl = new SandboxClassLoader(this, classLoaders);
	}

	public Sandbox(ClassLoader... classLoaders) {
		this(JvmSubset.DEFAULT, classLoaders);
	}

	public Sandbox(JvmSubset subset, URL... urls) {
		this(subset, new URLClassLoader(urls));
	}

	public Sandbox(URL... urls) {
		this(JvmSubset.DEFAULT, urls);
	}

	public Sandbox(JvmSubset subset) {
		this(subset, ClassLoader.getSystemClassLoader(), Sandbox.class.getClassLoader());
	}

	public Sandbox() {
		this(JvmSubset.DEFAULT);
	}
}
