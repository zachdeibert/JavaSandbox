package com.github.zachdeibert.javasandbox;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class SandboxedObject {
	private final Object obj;
	private final Class<?> cls;

	public final boolean equals(final SandboxedObject other) {
		return other == null ? false : obj == null ? other.obj == null : obj.equals(other.obj);
	}

	@Override
	public final boolean equals(final Object other) {
		if ( other instanceof SandboxedObject ) {
			return equals((SandboxedObject) other);
		} else {
			return false;
		}
	}

	@Override
	public final int hashCode() {
		return obj.hashCode();
	}

	public final boolean isString() {
		return obj instanceof String;
	}

	@Override
	public final String toString() {
		return obj.toString();
	}

	public final boolean isDouble() {
		return obj instanceof Double;
	}

	public final double toDouble() {
		if ( isDouble() ) {
			return (double) obj;
		} else {
			throw new ClassCastException("This object is not a double");
		}
	}

	public final boolean isFloat() {
		return obj instanceof Float;
	}

	public final float toFloat() {
		if ( isFloat() ) {
			return (float) obj;
		} else {
			throw new ClassCastException("This object is not a float");
		}
	}

	public final boolean isLong() {
		return obj instanceof Long;
	}

	public final long toLong() {
		if ( isLong() ) {
			return (long) obj;
		} else {
			throw new ClassCastException("This object is not a long");
		}
	}

	public final boolean isInt() {
		return obj instanceof Integer;
	}

	public final int toInt() {
		if ( isInt() ) {
			return (int) obj;
		} else {
			throw new ClassCastException("This object is not an int");
		}
	}

	public final boolean isShort() {
		return obj instanceof Short;
	}

	public final short toShort() {
		if ( isShort() ) {
			return (short) obj;
		} else {
			throw new ClassCastException("This object is not a short");
		}
	}

	public final boolean isChar() {
		return obj instanceof Character;
	}

	public final char toChar() {
		if ( isChar() ) {
			return (char) obj;
		} else {
			throw new ClassCastException("This object is not a char");
		}
	}

	public final boolean isByte() {
		return obj instanceof Byte;
	}

	public final byte toByte() {
		if ( isByte() ) {
			return (byte) obj;
		} else {
			throw new ClassCastException("This object is not a byte");
		}
	}

	public final boolean isBoolean() {
		return obj instanceof Boolean;
	}

	public final boolean toBoolean() {
		if ( isBoolean() ) {
			return (boolean) obj;
		} else {
			throw new ClassCastException("This object is not a boolean");
		}
	}

	public final boolean isPrimitive() {
		return isDouble() || isFloat() || isLong() || isInt() || isShort() || isChar() || isByte() || isBoolean();
	}

	public final boolean isSimple() {
		return isPrimitive() || isString();
	}

	public final boolean isObjectArray() {
		return obj instanceof Object[];
	}

	public final SandboxedObject[] toObjectArray() {
		if ( isObjectArray() ) {
			final Object[] before = (Object[]) obj;
			final SandboxedObject[] after = new SandboxedObject[before.length];
			for ( int i = 0; i < before.length; ++i ) {
				after[i] = new SandboxedObject(before[i]);
			}
			return after;
		} else {
			throw new ClassCastException("This object is not an array of objects");
		}
	}

	public final boolean isStringArray() {
		return obj instanceof String[];
	}

	public final String[] toStringArray() {
		if ( isStringArray() ) {
			return (String[]) obj;
		} else {
			throw new ClassCastException("This object is not an array of strings");
		}
	}

	public final boolean isDoubleArray() {
		return obj instanceof double[];
	}

	public final double[] toDoubleArray() {
		if ( isStringArray() ) {
			return (double[]) obj;
		} else {
			throw new ClassCastException("This object is not an array of doubles");
		}
	}

	public final boolean isFloatArray() {
		return obj instanceof float[];
	}

	public final float[] toFloatArray() {
		if ( isFloatArray() ) {
			return (float[]) obj;
		} else {
			throw new ClassCastException("This object is not an array of floats");
		}
	}

	public final boolean isLongArray() {
		return obj instanceof long[];
	}

	public final long[] toLongArray() {
		if ( isLongArray() ) {
			return (long[]) obj;
		} else {
			throw new ClassCastException("This object is not an array of longs");
		}
	}

	public final boolean isIntArray() {
		return obj instanceof int[];
	}

	public final int[] toIntArray() {
		if ( isIntArray() ) {
			return (int[]) obj;
		} else {
			throw new ClassCastException("This object is not an array of ints");
		}
	}

	public final boolean isShortArray() {
		return obj instanceof short[];
	}

	public final short[] toShortArray() {
		if ( isShortArray() ) {
			return (short[]) obj;
		} else {
			throw new ClassCastException("This object is not an array of shorts");
		}
	}

	public final boolean isCharArray() {
		return obj instanceof char[];
	}

	public final char[] toCharArray() {
		if ( isCharArray() ) {
			return (char[]) obj;
		} else {
			throw new ClassCastException("This object is not an array of chars");
		}
	}

	public final boolean isByteArray() {
		return obj instanceof byte[];
	}

	public final byte[] toByteArray() {
		if ( isByteArray() ) {
			return (byte[]) obj;
		} else {
			throw new ClassCastException("This object is not an array of bytes");
		}
	}

	public final boolean isBooleanArray() {
		return obj instanceof boolean[];
	}

	public final boolean[] toBooleanArray() {
		if ( isBooleanArray() ) {
			return (boolean[]) obj;
		} else {
			throw new ClassCastException("This object is not an array of booleans");
		}
	}

	public final boolean isPrimitiveArray() {
		return isDoubleArray() || isFloatArray() || isLongArray() || isIntArray() || isShortArray() || isCharArray()
				|| isByteArray() || isBooleanArray();
	}

	public final boolean isSimpleArray() {
		return isStringArray() || isPrimitiveArray();
	}

	public final boolean isArray() {
		return isObjectArray() || isSimpleArray();
	}

	public final boolean isConvertable() {
		return isSimple() || isSimpleArray();
	}

	public final void setField(final String name, final SandboxedObject obj, final boolean elevate)
			throws NoSuchFieldException, IllegalAccessException {
		final Field f = cls.getDeclaredField(name);
		if ( !f.isAccessible() ) {
			if ( elevate ) {
				f.setAccessible(true);
			} else {
				throw new IllegalAccessException("Field is not accessible");
			}
		}
		f.set(this.obj, obj);
	}

	public final void setField(final String name, final SandboxedObject obj)
			throws NoSuchFieldException, IllegalAccessException {
		setField(name, obj, false);
	}

	public final SandboxedObject getField(final String name, final boolean elevate)
			throws NoSuchFieldException, IllegalAccessException {
		final Field f = cls.getDeclaredField(name);
		if ( !f.isAccessible() ) {
			if ( elevate ) {
				f.setAccessible(true);
			} else {
				throw new IllegalAccessException("Field is not accessible");
			}
		}
		return new SandboxedObject(f.get(obj));
	}

	public final SandboxedObject getField(final String name) throws NoSuchFieldException, IllegalAccessException {
		return getField(name, false);
	}

	public final SandboxedObject callMethod(final String name, final boolean elevate, final Object... args)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		final Class<?>[] argTypes = new Class<?>[args.length];
		for ( int i = 0; i < args.length; ++i ) {
			argTypes[i] = args[i].getClass();
		}
		final Method m = cls.getDeclaredMethod(name, argTypes);
		if ( !m.isAccessible() ) {
			if ( elevate ) {
				m.setAccessible(true);
			} else {
				throw new IllegalAccessException("Method is not accessible");
			}
		}
		return new SandboxedObject(m.invoke(obj, args));
	}

	SandboxedObject(final Object obj) {
		this.obj = obj;
		cls = obj.getClass();
	}

	public SandboxedObject(final double obj) {
		this((Object) obj);
	}

	public SandboxedObject(final float obj) {
		this((Object) obj);
	}

	public SandboxedObject(final long obj) {
		this((Object) obj);
	}

	public SandboxedObject(final int obj) {
		this((Object) obj);
	}

	public SandboxedObject(final short obj) {
		this((Object) obj);
	}

	public SandboxedObject(final char obj) {
		this((Object) obj);
	}

	public SandboxedObject(final byte obj) {
		this((Object) obj);
	}

	public SandboxedObject(final boolean obj) {
		this((Object) obj);
	}

	public SandboxedObject(final String obj) {
		this((Object) obj);
	}

	public SandboxedObject(final double... objs) {
		this((Object) objs);
	}

	public SandboxedObject(final float... objs) {
		this((Object) objs);
	}

	public SandboxedObject(final long... objs) {
		this((Object) objs);
	}

	public SandboxedObject(final int... objs) {
		this((Object) objs);
	}

	public SandboxedObject(final short... objs) {
		this((Object) objs);
	}

	public SandboxedObject(final char... objs) {
		this((Object) objs);
	}

	public SandboxedObject(final byte... objs) {
		this((Object) objs);
	}

	public SandboxedObject(final boolean... objs) {
		this((Object) objs);
	}

	public SandboxedObject(final String... objs) {
		this((Object) objs);
	}

	private static final Object[] getObjects(final SandboxedObject[] objs) {
		final Object[] a = new Object[objs.length];
		for ( int i = 0; i < objs.length; ++i ) {
			a[i] = objs[i].obj;
		}
		return a;
	}

	public SandboxedObject(final SandboxedObject... objs) {
		this(getObjects(objs));
	}
}
