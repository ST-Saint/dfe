package edu.purdue.dfe;

public class SampleClass {
	Integer value;

	SampleClass(int v) {
		value = v;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SampleClass) {
			SampleClass oth = (SampleClass) obj;
			return value.equals(oth.value);
		}
		return false;
	}
}
