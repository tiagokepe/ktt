package ufpr.inf.kepe.util;

public class KnobFloat extends KnobNumber<Float>
{
	public KnobFloat() {}
	
	public KnobFloat(Float min, Float max, Float value) {
		super(min, max, value);
	}

	@Override
	public String type() {
		return "float";
	}

	@Override
	public Knob clone() {
		KnobFloat knob = new KnobFloat();
		knob.setMax(this.getMax());
		knob.setMin(this.getMin());
		knob.setValue(this.getValue());
		return knob;
	}
}