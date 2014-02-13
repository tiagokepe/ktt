package ufpr.inf.kepe.util;

public class KnobBoolean extends Knob<Boolean> {
	public KnobBoolean() {}
	
	public KnobBoolean(Boolean value) {
		super(value);
	}

	@Override
	public String type() {
		return "boolean";
	}

	@Override
	public Knob<Boolean> clone() {
		KnobBoolean knob = new KnobBoolean();
		knob.setValue(this.value);
		return knob;
	}
}