package com.github.zachdeibert.javasandbox;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

final class SandboxTransformer extends ClassVisitor {
	private final JvmSubset subset;
	private final String prefix;

	private final String transform(final String cls) {
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

	@Override
	public final void visit(final int version, final int access, String name, final String signature, String superName,
			final String[] interfaces) {
		name = transform(name);
		superName = transform(superName);
		for ( int i = 0; i < interfaces.length; ++i ) {
			interfaces[i] = transform(interfaces[i]);
		}
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public final FieldVisitor visitField(final int access, final String name, String desc, final String signature,
			final Object value) {
		desc = transform(Type.getType(desc).getClassName());
		return super.visitField(access, name, desc, signature, value);
	}

	@Override
	public final MethodVisitor visitMethod(final int access, final String name, String desc, final String signature,
			final String[] exceptions) {
		String ret = transform(Type.getReturnType(desc).getClassName());
		Type[] argTypes = Type.getArgumentTypes(desc);
		for ( int i = 0; i < argTypes.length; ++i ) {
			argTypes[i] = Type.getObjectType(transform(argTypes[i].getClassName()).replace('.', '/'));
		}
		desc = Type.getMethodDescriptor(Type.getObjectType(ret.replace('.', '/')), argTypes);
		for ( int i = 0; i < exceptions.length; ++i ) {
			exceptions[i] = transform(exceptions[i]);
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}

	SandboxTransformer(final ClassVisitor cv, final Sandbox sandbox) {
		super(Opcodes.ASM5, cv);
		this.subset = sandbox.subset;
		prefix = "com.github.zachdeibert.javasandbox.vm".concat(sandbox.vm).concat(".");
	}
}
