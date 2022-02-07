package edu.purdue.dfe.instrument;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public interface IProbeArrayStrategy {

	/**
	 * Creates code that stores the probe array instance in the given variable.
	 *
	 * @param mv
	 *            visitor to create code
	 * @param clinit
	 *            true in case of {@code <clinit>} method
	 * @param variable
	 *            variable index to store probe array to
	 * @return maximum stack size required by the generated code
	 */
	int storeInstance(MethodVisitor mv, boolean clinit, int variable);

	/**
	 * Adds additional class members required by this strategy. This method is
	 * called after all original members of the class has been processed.
	 *
	 * @param cv
	 *            visitor to create fields and methods
	 * @param probeCount
	 *            total number of probes required for this class
	 */
	void addMembers(ClassVisitor cv, int probeCount);

}
