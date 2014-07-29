package ufpr.inf.kepe.util;

public class KnobInt extends KnobNumber<Integer>
{
	public KnobInt() {
		
	}
	
	public KnobInt(Integer min, Integer max, Integer value) {
		super(min, max, value);
	}

	@Override
	public String type() {
		return "int";
	}

	@Override
	public Knob clone() {
		KnobInt knob = new KnobInt();
		knob.setMax(this.getMax());
		knob.setMin(this.getMin());
		knob.setValue(this.getValue());
		return knob;
	}
}