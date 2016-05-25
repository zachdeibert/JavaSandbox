package com.github.zachdeibert.javasandbox;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class JvmSubset {
	public static final JvmSubset ALL_PERMISSIONS = new JvmSubset(true);
	public static final JvmSubset DEFAULT = new JvmSubset(false, new JvmSubsetRule(true, "java.lang"),
			new JvmSubsetRule(false, "java.lang.reflect"), new JvmSubsetRule(true, "java.util"));
	private final boolean def;
	private final JvmSubsetRule[] rules;

	public final boolean isAllowed(final Class<?> cls) {
		boolean allow = def;
		for ( final JvmSubsetRule rule : rules ) {
			allow = rule.isAllowed(cls, allow);
		}
		return allow;
	}

	public final boolean isAllowed(final Constructor<?> ctor) {
		boolean allow = def;
		for ( final JvmSubsetRule rule : rules ) {
			allow = rule.isAllowed(ctor, allow);
		}
		return allow;
	}

	public final boolean isAllowed(final Method m) {
		boolean allow = def;
		for ( final JvmSubsetRule rule : rules ) {
			allow = rule.isAllowed(m, allow);
		}
		return allow;
	}

	public final boolean isAllowed(final Field f) {
		boolean allow = def;
		for ( final JvmSubsetRule rule : rules ) {
			allow = rule.isAllowed(f, allow);
		}
		return allow;
	}

	public JvmSubset(final boolean def, final JvmSubsetRule... rules) {
		this.def = def;
		this.rules = rules;
	}
}
