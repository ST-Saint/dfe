package edu.purdue.dfe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class DfeInstructionTransformer implements ClassFileTransformer {
    private static final String instDir = null;

    static Map<String, byte[]> instrumentedBytes = new TreeMap<>();

    private static String[] excludes = { "[", "java/lang", "janala", "org/objectweb/asm", "sun", "jdk",
            "java/util/function" };
    private static String[] includes = {};

    public static void premain(String agentArgs, Instrumentation inst) throws ClassNotFoundException {

        preloadClasses();

        inst.addTransformer(new DfeInstructionTransformer(), true);
        if (inst.isRetransformClassesSupported()) {
            for (Class clazz : inst.getAllLoadedClasses()) {
                try {
                    String cname = clazz.getName().replace(".", "/");
                    if (exclude(cname) == false) {
                        if (inst.isModifiableClass(clazz)) {
                            inst.retransformClasses(clazz);
                        } else {
                            System.err.println("[WARNING] Could not instrument " + clazz);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[WARNING] Could not instrument " + clazz);
                    e.printStackTrace();
                }
            }
        }
    }

    private static void preloadClasses() throws ClassNotFoundException {
        Class.forName("java.util.ArrayDeque");
        Class.forName("java.util.LinkedList");
        Class.forName("java.util.LinkedList$Node");
        Class.forName("java.util.LinkedList$ListItr");
        Class.forName("java.util.TreeMap");
        Class.forName("java.util.TreeMap$Entry");
        Class.forName("java.util.zip.ZipFile");
        Class.forName("java.util.jar.JarFile");
    }

    private static boolean exclude(String cname) {
        for (String e : excludes) {
            if (cname.startsWith(e)) {
                return true;
            }
        }
        for (String e : includes) {
            if (cname.startsWith(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    synchronized public byte[] transform(ClassLoader loader, String cname, Class<?> classBeingRedefined,
            ProtectionDomain d, byte[] cbuf) throws IllegalClassFormatException {

        if (cname == null) {
            // Do not instrument lambdas
            return null;
        }
        boolean toInstrument = !exclude(cname);

        if (toInstrument) {
            System.out.print("[INFO] ");
            if (classBeingRedefined != null) {
                System.out.print("* ");
            }
            System.out.println("Instrumenting: " + cname + "... ");
            // InstrumentatState.instance.setCid(cname.hashCode());

            if (instrumentedBytes.containsKey(cname)) {
                System.out.println(" Found in fast-cache!");
                return instrumentedBytes.get(cname);
            }

            if (instDir != null) {
                File cachedFile = new File(instDir + "/" + cname + ".instrumented.class");
                File referenceFile = new File(instDir + "/" + cname + ".original.class");
                if (cachedFile.exists() && referenceFile.exists()) {
                    try {
                        byte[] origBytes = Files.readAllBytes(referenceFile.toPath());
                        if (Arrays.equals(cbuf, origBytes)) {
                            byte[] instBytes = Files.readAllBytes(cachedFile.toPath());
                            System.out.println(" Found in disk-cache!");
                            instrumentedBytes.put(cname, instBytes);
                            return instBytes;
                        }
                    } catch (IOException e) {
                        System.err.println(" <cache error> ");
                    }
                }
            }

            byte[] ret = cbuf;
            try {

                ClassReader cr = new ClassReader(cbuf);
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                ClassVisitor cv = new InstructionClassAdapter(cw, cname);

                cr.accept(cv, 0);

                ret = cw.toByteArray();
            } catch (Throwable e) {
                System.err.println("\n[WARNING] Could not instrument " + cname);
                e.printStackTrace();
                return null;
            }

            System.out.println("Done!");
            instrumentedBytes.put(cname, ret);

            if (instDir != null) {
                try {
                    File cachedFile = new File(instDir + "/" + cname + ".instrumented.class");
                    File referenceFile = new File(instDir + "/" + cname + ".original.class");
                    File parent = new File(cachedFile.getParent());
                    parent.mkdirs();
                    try (FileOutputStream out = new FileOutputStream(cachedFile)) {
                        out.write(ret);
                    }
                    try (FileOutputStream out = new FileOutputStream(referenceFile)) {
                        out.write(cbuf);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ret;
        } else {
            return cbuf;
        }
    }
}
