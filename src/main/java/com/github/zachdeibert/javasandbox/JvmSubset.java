package com.github.zachdeibert.javasandbox;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class JvmSubset {
	public static final JvmSubset DEFAULT_INCLUDE = new JvmSubset(false, (JvmSubset) null,
			new JvmSubsetRule(true, "java"), new JvmSubsetRule(true, "javax"));
	public static final JvmSubset ALL_PERMISSIONS = load("com/github/zachdeibert/javasandbox/unrestricted.xml");
	public static final JvmSubset DEFAULT = load("com/github/zachdeibert/javasandbox/default.xml");
	private final boolean def;
	private final JvmSubset include;
	private final JvmSubsetRule[] rules;

	private static final JvmSubset load(final String name) {
		try {
			return new JvmSubsetBuilder().load(name).build();
		} catch ( final IOException ex ) {
			throw new RuntimeException(ex);
		}
	}

	public final boolean isIncluded(final String cls) {
		return include == null ? true : include.isAllowed(cls);
	}

	public final boolean isIncluded(final Class<?> cls) {
		return include == null ? true : include.isAllowed(cls);
	}

	public final boolean isIncluded(final Constructor<?> ctor) {
		return include == null ? true : include.isAllowed(ctor);
	}

	public final boolean isIncluded(final Method m) {
		return include == null ? true : include.isAllowed(m);
	}

	public final boolean isIncluded(final Field f) {
		return include == null ? true : include.isAllowed(f);
	}

	public final boolean isAllowed(final String cls) {
		if ( !isIncluded(cls) ) {
			return false;
		}
		boolean allow = def;
		for ( final JvmSubsetRule rule : rules ) {
			allow = rule.isAllowed(cls, allow);
		}
		return allow;
	}

	public final boolean isAllowed(final Class<?> cls) {
		if ( !isIncluded(cls) ) {
			return false;
		}
		boolean allow = def;
		for ( final JvmSubsetRule rule : rules ) {
			allow = rule.isAllowed(cls, allow);
		}
		return allow;
	}

	public final boolean isAllowed(final Constructor<?> ctor) {
		if ( !isIncluded(ctor) ) {
			return false;
		}
		boolean allow = def;
		for ( final JvmSubsetRule rule : rules ) {
			allow = rule.isAllowed(ctor, allow);
		}
		return allow;
	}

	public final boolean isAllowed(final Method m) {
		if ( !isIncluded(m) ) {
			return false;
		}
		boolean allow = def;
		for ( final JvmSubsetRule rule : rules ) {
			allow = rule.isAllowed(m, allow);
		}
		return allow;
	}

	public final boolean isAllowed(final Field f) {
		if ( !isIncluded(f) ) {
			return false;
		}
		boolean allow = def;
		for ( final JvmSubsetRule rule : rules ) {
			allow = rule.isAllowed(f, allow);
		}
		return allow;
	}

	public JvmSubset(final boolean def, final JvmSubset include, final JvmSubsetRule... rules) {
		this.def = def;
		this.include = include;
		this.rules = rules;
	}

	public JvmSubset(final boolean def, final JvmSubsetRule... rules) {
		this(def, DEFAULT_INCLUDE, rules);
	}
}
