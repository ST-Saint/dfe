package edu.purdue.dfe;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InstructionClassAdapter extends ClassVisitor {

    private final String className;
    private String superName;
    private Integer probeID = 0;

    private static final String probeFieldName = "_probeBitMap";
    private static final int probeAcc = Opcodes.ACC_SYNTHETIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC
            | Opcodes.ACC_TRANSIENT;
    private static final String probeDesc = "[Z";
    private String fieldDefault;
    private static final String clazzSignature = "_clazzSignature";
    private static final int clazzAcc = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC;

    private static String clazzName = "test";

    public InstructionClassAdapter(ClassVisitor cv, String className) {
        super(Opcodes.ASM9, cv);
        this.className = className;
    }

    // @Override
    // public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    //     // assert name.equals(this.className);
    //     this.superName = superName;
    //     System.out.println("name:" + name + "\nsignature: " + signature);
    //     clazzName = name;
    //     cv.visit(version, access, name, signature, superName, interfaces);
    // }

    // @Override
    // public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
    //     if (name.equals(fieldName)) {
    //         isFieldPresent = true;
    //         System.err.println("Duplicate insert");
    //     }
    //     return cv.visitField(access, name, desc, signature, value);
    // }

    // @Override
    // public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    //     MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
    //     if (mv != null) {
    //         return new InstructionMethodAdapter(mv, this, name, desc);
    //     }
    //     return null;
    // }

    @Override
    public void visitEnd() {
        cv.visitField(probeAcc, probeFieldName, probeDesc, null, null);
        System.out.println("visit here");
        cv.visitField(clazzAcc, clazzSignature, probeDesc, null, clazzName);
        super.visitEnd();
    }

    public Integer nextProbe() {
        return probeID++;
    }
}
