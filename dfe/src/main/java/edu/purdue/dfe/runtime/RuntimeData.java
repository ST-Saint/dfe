package edu.purdue.dfe.runtime;

import org.jacoco.core.internal.instr.InstrSupport;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import edu.purdue.dfe.data.DataStorage;
import edu.purdue.dfe.data.ProbeData;

public class RuntimeData {
	protected final DataStorage store;

	private long startTimeStamp;

	private String sessionId;

	public RuntimeData() {
		store = new DataStorage();
		sessionId = "<none>";
		startTimeStamp = System.currentTimeMillis();
	}

	public void getProbes(final Object[] args) {
		final Long classid = (Long) args[0];
		final String name = (String) args[1];
		final int probecount = ((Integer) args[2]).intValue();
		args[0] = getProbeData(classid, name, probecount).getProbes();
	}

	public ProbeData getProbeData(final Long id, final String name,
			final int probecount) {
		synchronized (store) {
			return store.get(id, name, probecount);
		}
	}

	@Override
	public boolean equals(final Object args) {
		if (args instanceof Object[]) {
			getProbes((Object[]) args);
		}
		return super.equals(args);
	}

	public static void generateAccessCall(final long classid,
			final String classname, final int probecount,
			final MethodVisitor mv) {
		// stack[0]: Ljava/lang/Object;

		generateArgumentArray(classid, classname, probecount, mv);

		// stack[1]: [Ljava/lang/Object;
		// stack[0]: Ljava/lang/Object;

		mv.visitInsn(Opcodes.DUP_X1);

		// stack[2]: [Ljava/lang/Object;
		// stack[1]: Ljava/lang/Object;
		// stack[0]: [Ljava/lang/Object;

		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "equals",
				"(Ljava/lang/Object;)Z", false);
		mv.visitInsn(Opcodes.POP);

		// stack[0]: [Ljava/lang/Object;

		mv.visitInsn(Opcodes.ICONST_0);
		mv.visitInsn(Opcodes.AALOAD);

		// stack[0]: [Z

		mv.visitTypeInsn(Opcodes.CHECKCAST, InstrSupport.DATAFIELD_DESC);
	}

	public static void generateArgumentArray(final long classid,
			final String classname, final int probecount,
			final MethodVisitor mv) {
		mv.visitInsn(Opcodes.ICONST_3);
		mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");

		// Class Id:
		mv.visitInsn(Opcodes.DUP);
		mv.visitInsn(Opcodes.ICONST_0);
		mv.visitLdcInsn(Long.valueOf(classid));
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf",
				"(J)Ljava/lang/Long;", false);
		mv.visitInsn(Opcodes.AASTORE);

		// Class Name:
		mv.visitInsn(Opcodes.DUP);
		mv.visitInsn(Opcodes.ICONST_1);
		mv.visitLdcInsn(classname);
		mv.visitInsn(Opcodes.AASTORE);

		// Probe Count:
		mv.visitInsn(Opcodes.DUP);
		mv.visitInsn(Opcodes.ICONST_2);
		InstrSupport.push(mv, probecount);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf",
				"(I)Ljava/lang/Integer;", false);
		mv.visitInsn(Opcodes.AASTORE);
	}

}
