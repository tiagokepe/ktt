package ufpr.inf.kepe.util;

public class FloatValue implements KnobValue
{
	private Float value;
	private Float max;
	private Float min;
	
	public Float getValue() {
		return value;
	}
	public void setValue(Float value) {
		this.value = value;
	}
	public Float getMax() {
		return max;
	}
	public void setMax(Float max) {
		this.max = max;
	}
	public Float getMin() {
		return min;
	}
	public void setMin(Float min) {
		this.min = min;
	}
	@Override
	public String type() {
		return "float";
	}
}