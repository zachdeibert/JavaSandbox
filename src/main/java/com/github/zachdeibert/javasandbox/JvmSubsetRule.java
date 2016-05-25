package com.github.zachdeibert.javasandbox;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JvmSubsetRule {
	private final boolean allow;
	private final String[] prefix;

	public static final String getDescriptor(final Class<?> cls) {
		return cls.getName();
	}

	private static final String getArguments(final Class<?>[] paramTypes) {
		final String[] params = new String[paramTypes.length];
		for ( int i = 0; i < paramTypes.length; ++i ) {
			params[i] = paramTypes[i].getName();
		}
		return String.join(", ", params);
	}

	public static final String getDescriptor(final Constructor<?> ctor) {
		return String.format("%s.%s(%s)", getDescriptor(ctor.getDeclaringClass()),
				ctor.getDeclaringClass().getSimpleName(), getArguments(ctor.getParameterTypes()));
	}

	public static final String getDescriptor(final Method m) {
		return String.format("%s.%s(%s)", getDescriptor(m.getDeclaringClass()), m.getName(),
				getArguments(m.getParameterTypes()));
	}

	public static final String getDescriptor(final Field f) {
		return String.format("%s.%s", getDescriptor(f.getDeclaringClass()), f.getName());
	}

	public boolean isAllowed(final String desc, final boolean def) {
		final String[] parts = desc.split("\\.");
		if ( parts.length < prefix.length ) {
			return def;
		} else {
			for ( int i = 0; i < prefix.length; ++i ) {
				if ( !parts[i].equals(prefix[i]) ) {
					return def;
				}
			}
			return allow;
		}
	}

	public final boolean isAllowed(final Class<?> cls, final boolean def) {
		return isAllowed(getDescriptor(cls), def);
	}

	public final boolean isAllowed(final Constructor<?> ctor, final boolean def) {
		return isAllowed(getDescriptor(ctor), def);
	}

	public final boolean isAllowed(final Method m, final boolean def) {
		return isAllowed(getDescriptor(m), def);
	}

	public final boolean isAllowed(final Field f, final boolean def) {
		return isAllowed(getDescriptor(f), def);
	}

	public JvmSubsetRule(final boolean allow, final String prefix) {
		this.allow = allow;
		this.prefix = prefix.split("\\.");
	}
}
