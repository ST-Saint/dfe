package edu.purdue.dfe.instrument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import edu.purdue.dfe.instrument.CRC64;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class DfeTransformer implements ClassFileTransformer {
	private static final String instDir = null;

	static Map<String, byte[]> instrumentedBytes = new TreeMap<>();

	private static String[] excludes = { "[", "java/lang", "janala",
			"org/objectweb/asm", "sun", "jdk", "java/util/function" };
	private static String[] includes = {};

	private static final String ADDRESS = "localhost";

	private static final int PORT = 6324;

	private static class Handler implements Runnable {

		private final Socket socket;

		private final int maxn = 10240;

		private byte[] buffer;

		Handler(final Socket socket) throws IOException {
			this.socket = socket;
			buffer = new byte[maxn];
		}

		@Override
		public void run() {
			while (true) {
				try {
					OutputStream out = socket.getOutputStream();
					InputStream in = socket.getInputStream();
					in.read(buffer);
					System.out.println("Client get: " + new String(buffer));
					System.out.println("Client send "
							+ new String("test cleint".getBytes()));
					out.write("test client".getBytes());
				} catch (final IOException e) {
					// e.printStackTrace();
				}
				// break;
			}
		}

	}

	public static void startDfeClient() {
		try {
			final Socket socket = new Socket(InetAddress.getByName(ADDRESS),
					PORT);
			// socket.
			// final ServerSocket socket = new ServerSocket(PORT, 0,
			// InetAddress.getByName(ADDRESS));
			final Handler handler = new Handler(socket);
			new Thread(handler).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void premain(String agentArgs, Instrumentation inst)
			throws ClassNotFoundException {

		preloadClasses();

		startDfeClient();

		inst.addTransformer(new DfeTransformer(), true);
		if (inst.isRetransformClassesSupported()) {
			for (Class clazz : inst.getAllLoadedClasses()) {
				try {
					String cname = clazz.getName().replace(".", "/");
					if (exclude(cname) == false) {
						if (inst.isModifiableClass(clazz)) {
							inst.retransformClasses(clazz);
						} else {
							System.err.println(
									"[WARNING] Could not instrument " + clazz);
						}
					}
				} catch (Exception e) {
					System.err
							.println("[WARNING] Could not instrument " + clazz);
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
	synchronized public byte[] transform(ClassLoader loader, String cname,
			Class<?> classBeingRedefined, ProtectionDomain d, byte[] cbuf)
			throws IllegalClassFormatException {

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
				File cachedFile = new File(
						instDir + "/" + cname + ".instrumented.class");
				File referenceFile = new File(
						instDir + "/" + cname + ".original.class");
				if (cachedFile.exists() && referenceFile.exists()) {
					try {
						byte[] origBytes = Files
								.readAllBytes(referenceFile.toPath());
						if (Arrays.equals(cbuf, origBytes)) {
							byte[] instBytes = Files
									.readAllBytes(cachedFile.toPath());
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
				Long classId = CRC64.classId(cbuf);
				ClassReader cr = new ClassReader(cbuf);
				ClassWriter cw = new ClassWriter(cr,
						ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
				ClassVisitor cv = new InstructionClassAdapter(cw, cname,
						classId);

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
					File cachedFile = new File(
							instDir + "/" + cname + ".instrumented.class");
					File referenceFile = new File(
							instDir + "/" + cname + ".original.class");
					File parent = new File(cachedFile.getParent());
					parent.mkdirs();
					try (FileOutputStream out = new FileOutputStream(
							cachedFile)) {
						out.write(ret);
					}
					try (FileOutputStream out = new FileOutputStream(
							referenceFile)) {
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
