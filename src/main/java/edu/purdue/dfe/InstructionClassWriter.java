package edu.purdue.dfe;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class InstructionClassWriter extends ClassWriter{
    private ClassLoader loader;

    public InstructionClassWriter(ClassReader cr, ClassLoader loader, final int flags) {
        super(cr, flags);
        this.loader = loader != null ? loader : ClassLoader.getSystemClassLoader();
    }

}
