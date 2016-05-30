package com.github.zachdeibert.javasandbox;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JvmSubsetTest {
	private JvmSubset subset;

	@Before
	public void setUp() throws Exception {
		subset = new JvmSubsetBuilder().load("com/github/zachdeibert/javasandbox/subsetTest.xml").build();
	}

	@After
	public void tearDown() throws Exception {
		subset = null;
	}

	@Test
	public void testIsIncludedString() {
		Assert.assertTrue(subset.isIncluded("java.lang.Object"));
		Assert.assertFalse(subset.isIncluded("java.io.FileInputStream"));
		Assert.assertFalse(subset.isIncluded("javax.swing.JLabel"));
	}

	@Test
	public void testIsIncludedClassOfQ() {
		Assert.assertTrue(subset.isIncluded(Object.class));
		Assert.assertFalse(subset.isIncluded(FileInputStream.class));
		Assert.assertFalse(subset.isIncluded(JLabel.class));
	}

	@Test
	public void testIsIncludedConstructorOfQ() throws NoSuchMethodException, SecurityException {
		Assert.assertTrue(subset.isIncluded(Object.class.getConstructor()));
		Assert.assertFalse(subset.isIncluded(FileInputStream.class.getConstructor(File.class)));
		Assert.assertTrue(subset.isIncluded(JLabel.class.getConstructor(String.class)));
	}

	@Test
	public void testIsIncludedMethod() throws NoSuchMethodException, SecurityException {
		Assert.assertFalse(subset.isIncluded(Object.class.getMethod("equals", Object.class)));
		Assert.assertFalse(subset.isIncluded(FileInputStream.class.getMethod("close")));
		Assert.assertFalse(subset.isIncluded(JLabel.class.getMethod("getText")));
	}

	@Test
	public void testIsIncludedField() throws NoSuchFieldException, SecurityException {
		Assert.assertTrue(subset.isIncluded(Throwable.class.getDeclaredField("backtrace")));
		Assert.assertFalse(subset.isIncluded(FileInputStream.class.getDeclaredField("fd")));
		Assert.assertTrue(subset.isIncluded(JLabel.class.getDeclaredField("text")));
	}

	@Test
	public void testIsAllowedString() {
		Assert.assertTrue(subset.isAllowed("java.util.List"));
		Assert.assertFalse(subset.isAllowed("java.lang.Object"));
		Assert.assertTrue(subset.isAllowed("java.awt.Color"));
	}

	@Test
	public void testIsAllowedClassOfQ() {
		Assert.assertTrue(subset.isAllowed(List.class));
		Assert.assertFalse(subset.isAllowed(Object.class));
		Assert.assertTrue(subset.isAllowed(Color.class));
	}

	@Test
	public void testIsAllowedConstructorOfQ() throws NoSuchMethodException, SecurityException {
		Assert.assertTrue(subset.isAllowed(ArrayList.class.getConstructor()));
		Assert.assertFalse(subset.isAllowed(Object.class.getConstructor()));
		Assert.assertFalse(subset.isAllowed(Color.class.getConstructor(Integer.class)));
	}

	@Test
	public void testIsAllowedMethod() throws NoSuchMethodException, SecurityException {
		Assert.assertTrue(subset.isAllowed(List.class.getMethod("size")));
		Assert.assertTrue(subset.isAllowed(Object.class.getMethod("hashCode")));
		Assert.assertTrue(subset.isAllowed(Color.class.getMethod("getRed")));
	}

	@Test
	public void testIsAllowedField() throws NoSuchFieldException, SecurityException {
		Assert.assertTrue(subset.isAllowed(ArrayList.class.getDeclaredField("size")));
		Assert.assertFalse(subset.isAllowed(Throwable.class.getDeclaredField("backtrace")));
		Assert.assertFalse(subset.isAllowed(Color.class.getDeclaredField("value")));
	}
}
