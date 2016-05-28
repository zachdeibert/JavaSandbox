package com.github.zachdeibert.javasandbox;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

final class SandboxTransformer extends ClassVisitor {
	private final JvmSubset subset;
	private final String prefix;

	private final String transformClass(String cls) {
		cls = cls.replace('/', '.');
		if ( subset.isIncluded(cls) ) {
			if ( subset.isAllowed(cls) ) {
				return cls;
			} else {
				throw new IllegalReferenceException(String.format("Code does not have permission to use %s", cls));
			}
		} else {
			return prefix.concat(cls);
		}
	}

	private final String transformClassDesc(String desc) {
		return transformClass(desc).replace('.', '/');
	}

	private final Type transform(final Type cls) {
		switch ( cls.getSort() ) {
		case Type.ARRAY:
			return Type.getType("[".concat(transform(cls.getElementType()).getDescriptor()));
		case Type.METHOD:
			Type ret = transform(cls.getReturnType());
			Type[] argTypes = cls.getArgumentTypes();
			for ( int i = 0; i < argTypes.length; ++i ) {
				argTypes[i] = transform(argTypes[i]);
			}
			return Type.getMethodType(ret, argTypes);
		case Type.OBJECT:
			return Type.getObjectType(transformClass(cls.getClassName()).replace('.', '/'));
		default:
			return cls;
		}
	}

	private final String transformMethodDesc(final String desc) {
		return transform(Type.getMethodType(desc)).getDescriptor();
	}

	@Override
	public final void visit(final int version, final int access, String name, final String signature, String superName,
			final String[] interfaces) {
		name = transformClassDesc(name);
		superName = transformClassDesc(superName);
		for ( int i = 0; i < interfaces.length; ++i ) {
			interfaces[i] = transformClass(interfaces[i]);
		}
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public final FieldVisitor visitField(final int access, final String name, String desc, final String signature,
			final Object value) {
		desc = transformClassDesc(desc);
		return super.visitField(access, name, desc, signature, value);
	}

	@Override
	public final MethodVisitor visitMethod(final int access, final String name, String desc, final String signature,
			final String[] exceptions) {
		desc = transformMethodDesc(desc);
		if ( exceptions != null ) {
			for ( int i = 0; i < exceptions.length; ++i ) {
				exceptions[i] = transformClass(exceptions[i]);
			}
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}

	SandboxTransformer(final ClassVisitor cv, final Sandbox sandbox) {
		super(Opcodes.ASM5, cv);
		this.subset = sandbox.subset;
		prefix = "com.github.zachdeibert.javasandbox.vm".concat(sandbox.vm).concat(".");
	}
}
