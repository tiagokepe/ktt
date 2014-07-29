package ufpr.inf.kepe.util;

public class IntValue implements KnobValue
{
	private Integer value;
	private Integer max;
	private Integer min;

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	@Override
	public String type() {
		return "int";
	}
}