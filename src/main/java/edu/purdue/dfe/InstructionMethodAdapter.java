package edu.purdue.dfe;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
// import org.objectweb.asm.Opcodes.*;

public class InstructionMethodAdapter extends MethodVisitor implements Opcodes {
    static Integer iid = 0;
    private ClassVisitor cv;

    public InstructionMethodAdapter(MethodVisitor mv, ClassVisitor cv, String className, String methodName) {
        super(Opcodes.ASM9, mv);
        this.cv = cv;
    }


    @Override
    public void visitJumpInsn(int opcode, Label label) {
        switch (opcode) {
        case IFEQ:
            break;
        case IFNE:
            break;
        }
    }
}
