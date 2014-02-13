package ufpr.inf.kepe.util;

public abstract class Knob<TYPE> {
	public TYPE value;
	
	public Knob() {}
	
	public Knob(TYPE value) {
		this.value = value;
	}

	public TYPE getValue() {
		return value;
	}

	public void setValue(TYPE value) {
		this.value = value;
	}
	
	public abstract String type();
	public abstract Knob<TYPE> clone();
}