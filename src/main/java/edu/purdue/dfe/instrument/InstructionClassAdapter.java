package edu.purdue.dfe.instrument;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

import edu.purdue.dfe.DfeProperty;
import edu.purdue.dfe.Instrument.InstructionMethodAdapter;
import edu.purdue.dfe.runtime.RuntimeData;

public class InstructionClassAdapter extends ClassVisitor {

    private final String className;
    private String superName;
    private Integer probeId = 0;

    private static final String probeFieldName = "_probeBitMap";
    private static final int probeAcc = Opcodes.ACC_SYNTHETIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC
            | Opcodes.ACC_TRANSIENT;
    static final String probeDesc = "[Z";
    private String fieldDefault;
    private Long classId;
    private static String key;
    private static final String clazzSignature = "_clazzSignature";
    private static final int clazzAcc = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC;
    public static final int INITMETHOD_ACC = Opcodes.ACC_SYNTHETIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC;
    public static final String INITMETHOD_DESC = "()[Z";
    public static final String INITMETHOD_NAME = "$jacocoInit";

    private static final Object[] FRAME_LOCALS_EMPTY = new Object[0];
    private static final Object[] FRAME_STACK_ARRZ = new Object[] { probeDesc };

    private String clazzName;;

    public InstructionClassAdapter(ClassVisitor cv, String className, Long classId) {
        super(Opcodes.ASM9, cv);
        this.className = className;
        this.key = "dfe-" + Integer.toHexString(hashCode());
        this.classId = classId;
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature,
            final String superName, final String[] interfaces) {
        this.clazzName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public final MethodVisitor visitMethod(final int access, final String name, final String desc,
            final String signature, final String[] exceptions) {
        final MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv == null) {
            // FIXME We need to visit the method in any case, otherwise probe ids
            // are not reproducible
            // methodProbes = EMPTY_METHOD_PROBES_VISITOR;
            return null;
        } else {
            return new InstructionMethodAdapter(mv,cv, clazzName, name, desc,signature, exceptions);
        }
    }

    private void createInitMethod(final ClassVisitor cv, final int probeCount) {
        final MethodVisitor mv = cv.visitMethod(INITMETHOD_ACC, INITMETHOD_NAME, INITMETHOD_DESC, null, null);
        mv.visitCode();

        // Load the value of the static data field:
        mv.visitFieldInsn(Opcodes.GETSTATIC, className, probeFieldName, probeDesc);
        mv.visitInsn(Opcodes.DUP);

        // Stack[1]: [Z
        // Stack[0]: [Z

        // Skip initialization when we already have a data array:
        final Label alreadyInitialized = new Label();
        mv.visitJumpInsn(Opcodes.IFNONNULL, alreadyInitialized);

        // Stack[0]: [Z

        mv.visitInsn(Opcodes.POP);
        final int size = genInitializeDataField(mv, probeCount);

        // Stack[0]: [Z

        // Return the class' probe array:
        mv.visitFrame(Opcodes.F_NEW, 0, FRAME_LOCALS_EMPTY, 1, FRAME_STACK_ARRZ);
        mv.visitLabel(alreadyInitialized);
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitMaxs(Math.max(size, 2), 0); // Maximum local stack size is 2
        mv.visitEnd();
    }

    private int genInitializeDataField(final MethodVisitor mv, final int probeCount) {
        final int size = systemPropertyDataAccessor(classId, className, probeCount, mv);

        // Stack[0]: [Z

        mv.visitInsn(Opcodes.DUP);

        // Stack[1]: [Z

        mv.visitFieldInsn(Opcodes.PUTSTATIC, className, probeFieldName, probeDesc);

        // Stack[0]: [Z

        return Math.max(size, 2); // Maximum local stack size is 2
    }

    private int systemPropertyDataAccessor(Long classId, String className, int probeCount, MethodVisitor mv) {
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "getProperties", "()Ljava/util/Properties;",
                false);

        // Stack[0]: Ljava/util/Properties;

        mv.visitLdcInsn(key);

        // Stack[1]: Ljava/lang/String;
        // Stack[0]: Ljava/util/Properties;

        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Properties", "get",
                "(Ljava/lang/Object;)Ljava/lang/Object;", false);

        // Stack[0]: Ljava/lang/Object;

        RuntimeData.generateAccessCall(classId, className, probeId, mv);

        // Stack[0]: [Z

        return 6; // Maximum local stack size is 3
    }

    @Override
    public void visitEnd() {
        addMembers(cv, probeId);
        // cv.visitField(probeAcc, probeFieldName, probeDesc, null, null);
        // System.out.println("visit here");
        // cv.visitField(clazzAcc, clazzSignature, probeDesc, null, clazzName);
        super.visitEnd();
    }

    private void createDataField(final ClassVisitor cv) {
        cv.visitField(DfeProperty.DATAFIELD_ACC, DfeProperty.DATAFIELD_NAME, DfeProperty.DATAFIELD_DESC, null, null);
    }

    public Integer nextProbe() {
        return probeId++;
    }

    public int storeInstance(final MethodVisitor mv, final boolean clinit, final int variable) {
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, className, DfeProperty.INITMETHOD_NAME, DfeProperty.INITMETHOD_DESC,
                false);
        mv.visitVarInsn(Opcodes.ASTORE, variable);
        return 1;
    }

    public void addMembers(final ClassVisitor cv, final int probeCount) {
        createDataField(cv);
        createInitMethod(cv, probeCount);
    }
}
