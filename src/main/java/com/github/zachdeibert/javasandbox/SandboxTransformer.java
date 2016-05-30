package com.github.zachdeibert.javasandbox;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

final class SandboxTransformer extends ClassVisitor {
	private final class Method extends MethodVisitor {
		@Override
		public final AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath, String desc,
				final boolean visible) {
			desc = transformClassDesc(desc);
			return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
		}

		@Override
		public final AnnotationVisitor visitParameterAnnotation(final int parameter, String desc,
				final boolean visible) {
			desc = transformClassDesc(desc);
			return super.visitParameterAnnotation(parameter, desc, visible);
		}

		@Override
		public final void visitTypeInsn(final int opcode, String type) {
			type = transformClassDesc(type);
			super.visitTypeInsn(opcode, type);
		}

		@Override
		public final void visitFieldInsn(final int opcode, String owner, final String name, String desc) {
			owner = transformClassDesc(owner);
			desc = transformClassDesc(desc);
			super.visitFieldInsn(opcode, owner, name, desc);
		}

		@Override
		public final void visitMethodInsn(final int opcode, String owner, final String name, String desc,
				final boolean itf) {
			owner = transformClassDesc(owner);
			desc = transformMethodDesc(desc);
			super.visitMethodInsn(opcode, owner, name, desc, itf);
		}

		@Override
		public final void visitInvokeDynamicInsn(final String name, String desc, Handle bsm, final Object... bsmArgs) {
			desc = transformMethodDesc(desc);
			bsm = transformHandle(bsm);
			for ( int i = 0; i < bsmArgs.length; ++i ) {
				if ( bsmArgs[i] instanceof Handle ) {
					bsmArgs[i] = transformHandle((Handle) bsmArgs[i]);
				}
			}
			super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
		}

		@Override
		public final void visitMultiANewArrayInsn(String desc, final int dims) {
			desc = transformClassDesc(desc);
			super.visitMultiANewArrayInsn(desc, dims);
		}

		@Override
		public final AnnotationVisitor visitInsnAnnotation(final int typeRef, final TypePath typePath, String desc,
				final boolean visible) {
			desc = transformClassDesc(desc);
			return super.visitInsnAnnotation(typeRef, typePath, desc, visible);
		}

		@Override
		public final void visitLocalVariable(final String name, String desc, final String signature, final Label start,
				final Label end, final int index) {
			desc = transformClassDesc(desc);
			super.visitLocalVariable(name, desc, signature, start, end, index);
		}

		public Method(final MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
		}
	}

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
		boolean t;
		if ( desc.length() == 1 ) {
			return desc;
		} else if ( (t = desc.startsWith("[") || desc.endsWith(";")) ) {
			desc = Type.getType(desc).getClassName();
		}
		desc = transformClass(desc).replace('.', '/');
		if ( t ) {
			desc = Type.getObjectType(desc).getDescriptor();
		}
		return desc;
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

	private final Handle transformHandle(final Handle h) {
		return new Handle(h.getTag(), transformClassDesc(h.getOwner()), h.getName(), transformMethodDesc(h.getDesc()),
				h.isInterface());
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
				exceptions[i] = transformClassDesc(exceptions[i]);
			}
		}
		return new Method(super.visitMethod(access, name, desc, signature, exceptions));
	}

	SandboxTransformer(final ClassVisitor cv, final Sandbox sandbox) {
		super(Opcodes.ASM5, cv);
		this.subset = sandbox.subset;
		prefix = "com.github.zachdeibert.javasandbox.vm".concat(sandbox.vm).concat(".");
	}
}
