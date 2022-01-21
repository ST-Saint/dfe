package edu.purdue.dfe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import org.jacoco.core.internal.data.CRC64;
import org.jacoco.core.internal.flow.ClassProbesAdapter;
import org.jacoco.core.internal.instr.ClassInstrumenter;
import org.jacoco.core.internal.instr.IProbeArrayStrategy;
import org.jacoco.core.internal.instr.InstrSupport;
import org.jacoco.core.internal.instr.ProbeArrayStrategyFactory;
import org.jacoco.core.runtime.IExecutionDataAccessorGenerator;
import org.jacoco.core.runtime.RuntimeData;
import org.jacoco.core.runtime.SystemPropertiesRuntime;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

public class Instance {

        private static boolean[] getBooleans() {
                // System.out.println(System.getProperties().hashCode());
                Object[] objectArray = new Object[] { 5711681868228170062L, "edu/purdue/dfe/TestSample", 15 };
                System.getProperties().get("jacoco-2eee9593").equals(objectArray);
                // System.out.println(System.getProperties().hashCode());
                // System.getProperties().put("jacoco-2eee9593", new Object[]{new boolean[1024], "edu/purdue/dfe/TestSample", 15});
                return (boolean[])objectArray[0];
                // Object[] obj = (Object[]) System.getProperties().get("jacoco-2eee9593");
                // obj.equals(objectArray);
                // return (boolean[]) obj[0];
        }

        private static void testGetBooleans1() {
                boolean[] a = getBooleans();
                for (int i = 0; i < 15; ++i) {
                        System.out.print(a[i] + " ");
                }
                a[0] = true;
                a[5] = true;
                System.out.println();
        }

        private static void testGetBooleans2() {
                boolean[] a = getBooleans();
                for (int i = 0; i < 15; ++i) {
                        System.out.print(a[i] + " ");
                }
        }

        public static void main(String[] args) throws Exception {
                System.getProperties().put("jacoco-2eee9593", new RuntimeData());
                testGetBooleans1();
                testGetBooleans2();
                // sampleInstr();
                // new Instance().jacocoInstr();
        }

        private static void sampleInstr() throws IOException {
                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"));
                ClassReader reader = new ClassReader("edu.purdue.dfe.TestSample");
                InstructionClassWriter icw = new InstructionClassWriter(reader, null,
                                ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                TraceClassVisitor tcv = new TraceClassVisitor(icw, printWriter);
                final ClassWriter writer = new ClassWriter(reader, 0) {
                        @Override
                        protected String getCommonSuperClass(final String type1, final String type2) {
                                throw new IllegalStateException();
                        }
                };
                InstructionClassAdapter ica = new InstructionClassAdapter(writer, "class");
                reader.accept(ica, 0);
                String dir = System.getProperty("user.dir");
                File instrFile = new File(dir + "/instrumented.class");
                System.out.println("write to " + instrFile);
                if (!instrFile.exists()) {
                        instrFile.createNewFile();
                }
                FileOutputStream fo = new FileOutputStream(instrFile);
                fo.write(writer.toByteArray());
        }

        private void jacocoInstr() throws Exception {
                // final long classId = CRC64.classId(reader.b);
                //
                ClassReader reader = new ClassReader("edu.purdue.dfe.TestSample");
                final long classId = 0;
                final ClassWriter writer = new ClassWriter(reader, 0) {
                        @Override
                        protected String getCommonSuperClass(final String type1, final String type2) {
                                throw new IllegalStateException();
                        }
                };

                IExecutionDataAccessorGenerator accessorGenerator = new SystemPropertiesRuntime();
                final IProbeArrayStrategy strategy = ProbeArrayStrategyFactory.createFor(classId, reader,
                                accessorGenerator);
                final int version = InstrSupport.getMajorVersion(reader);
                final ClassVisitor visitor = new ClassProbesAdapter(new ClassInstrumenter(strategy, writer),
                                InstrSupport.needsFrames(version));
                reader.accept(visitor, ClassReader.EXPAND_FRAMES);

                // reader.accept(ica, 0);
                String dir = System.getProperty("user.dir");
                File instrFile = new File(dir + "/instrumented.class");
                System.out.println("write to " + instrFile);
                if (!instrFile.exists()) {
                        instrFile.createNewFile();
                }
                FileOutputStream fo = new FileOutputStream(instrFile);
                fo.write(writer.toByteArray());
                byte[] testSampleBytes = writer.toByteArray();
                // URL classUrl = new URL("file:///home/yayu/Project/Upgrade-Fuzzing/dfe/distributedfuzzingengine/target/classes/edu/purdue/dfe/TestSample.class");
                String classPath = "home/yayu/Project/Upgrade-Fuzzing/dfe/distributedfuzzingengine/target/classes/edu/purdue/dfe/TestSample.class";
                DfeClassLoader dfl = new DfeClassLoader(new String[]{}, getClass().getClassLoader());
                // ClassLoader dfl =
                Class<?> testSample =  dfl.testDefineClass(testSampleBytes);
                Method testString = testSample.getMethod("testString", null);
                testString.invoke(null);

        }
}