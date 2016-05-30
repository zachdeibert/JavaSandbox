package com.github.zachdeibert.javasandbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JvmSubsetBuilder {
	private boolean def;
	private final List<JvmSubsetRule> rules;
	private final JvmSubsetBuilder includes;
	private final JvmSubsetBuilder parent;

	public JvmSubsetBuilder setDef(boolean def) {
		this.def = def;
		return this;
	}

	public JvmSubsetBuilder addRules(JvmSubsetRule... rules) {
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

	private JvmSubsetBuilder(JvmSubsetBuilder parent) {
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
