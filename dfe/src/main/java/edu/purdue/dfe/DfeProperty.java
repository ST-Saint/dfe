package edu.purdue.dfe;

import org.objectweb.asm.Opcodes;

public class DfeProperty {

	public static final String CLINIT_NAME = "<clinit>";
	public static final String CLINIT_DESC = "()V";
	public static final int CLINIT_ACC = Opcodes.ACC_SYNTHETIC
			| Opcodes.ACC_STATIC;

	public static final String DATAFIELD_NAME = "$jacocoData";
	public static final int DATAFIELD_ACC = Opcodes.ACC_SYNTHETIC
			| Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_TRANSIENT;
	public static final int DATAFIELD_INTF_ACC = Opcodes.ACC_SYNTHETIC
			| Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
	public static final String DATAFIELD_DESC = "[Z";

	public static final String INITMETHOD_NAME = "$jacocoInit";

	public static final String INITMETHOD_DESC = "()[Z";

	public static final int INITMETHOD_ACC = Opcodes.ACC_SYNTHETIC
			| Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC;
}
