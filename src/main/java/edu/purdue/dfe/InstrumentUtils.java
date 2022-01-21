package edu.purdue.dfe;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InstrumentUtils {

    /*
     * add push instruction according to the value
     * if -1 <= value <= 5, push ICONST_x
     * if -128 <= value <= 127, push BIPUSH x (byte integer push)
     * if -32768 <= value <= 32767, push SIPUSH x (short integer push)
     * else -=2147483648 <= value <= 2147483647, LDC x (LoaD Const)
     * @param mv
     * @param value
     */
    public static void push(final MethodVisitor mv, final int value) {
        if (value >= -1 && value <= 5) {
            mv.visitInsn(Opcodes.ICONST_0 + value);
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.BIPUSH, value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.SIPUSH, value);
        } else {
            mv.visitLdcInsn(Integer.valueOf(value));
        }
    }
}
