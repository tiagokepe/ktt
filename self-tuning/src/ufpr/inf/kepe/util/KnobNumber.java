package ufpr.inf.kepe.util;

public abstract class KnobNumber<TYPE> extends Knob<TYPE>
{
	public TYPE max;
	public TYPE min;
	
	public KnobNumber() {}
	
	public KnobNumber(TYPE min, TYPE max, TYPE value) {
		super(value);
		this.max = max;
		this.min = min;
	}
	
	public TYPE getMax() {
		return max;
	}
	public void setMax(TYPE max) {
		this.max = max;
	}
	public TYPE getMin() {
		return min;
	}
	public void setMin(TYPE min) {
		this.min = min;
	}
}
