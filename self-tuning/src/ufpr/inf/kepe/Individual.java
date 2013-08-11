package ufpr.inf.kepe;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ufpr.inf.kepe.util.FloatValue;
import ufpr.inf.kepe.util.IntValue;
import ufpr.inf.kepe.util.KnobValue;

public class Individual {
	private Map<String, KnobValue> mapKnobs;
	private double executionTime;

	public Individual() {
		this.mapKnobs = new HashMap<String, KnobValue>();
	}

	public Individual(Map<String, KnobValue> mapKnobs) {
		this.mapKnobs = mapKnobs;
	}

	public void autoMutation() {
		String[] keySet = (String[]) mapKnobs.keySet().toArray(new String[0]);
		Random rand = new Random();
		int posKey = rand.nextInt(keySet.length);
		String keyMutate = keySet[posKey];
		KnobValue value = mapKnobs.get(keyMutate);

		if (value instanceof IntValue)
		{
			rand = new Random();
			int major = ((IntValue) value).getMax();
			int valueMutate = rand.nextInt(major);
			((IntValue) value).setValue(valueMutate);
			mapKnobs.put(keyMutate, value);
		}
		else if (value instanceof FloatValue)
		{
			rand = new Random();
			Float valueMutate = rand.nextFloat();
			((FloatValue) value).setValue(valueMutate);
			mapKnobs.put(keyMutate, value);
		}
/*		else if (value instanceof Boolean) {
			rand = new Random();
			Boolean valueMutate = rand.nextBoolean();
			mapKnobs.put(keyMutate, valueMutate);
		}*/
	}

	public Individual clone() {
		Individual indivCloned = new Individual();
		Map<String, KnobValue> copyKnobs = new HashMap<String, KnobValue>();
		String key;
		Object oldValue;
		for (Map.Entry<String, KnobValue> entry : this.mapKnobs.entrySet()) {
			key = entry.getKey();
			oldValue = entry.getValue();
			if (oldValue instanceof IntValue)
			{
				IntValue value = new IntValue();
				value.setValue(((IntValue) oldValue).getValue());
				value.setMax(((IntValue) oldValue).getMax());
				value.setMin(((IntValue) oldValue).getMin());

				copyKnobs.put(key, value);
			}
			else if (oldValue instanceof FloatValue)
			{
				FloatValue value = new FloatValue();
				value.setValue(((FloatValue) oldValue).getValue());
				value.setMax(((FloatValue) oldValue).getMax());
				value.setMin(((FloatValue) oldValue).getMin());
				
				copyKnobs.put(key, value);
			}
/*			else {
				Boolean value = new Boolean((Boolean) oldValue);
				copyKnobs.put(key, value);
			}*/
		}

		indivCloned.setMapKnobs(copyKnobs);
		return indivCloned;
	}

	public Map<String, KnobValue> getMapKnobs() {
		return mapKnobs;
	}

	public void setMapKnobs(Map<String, KnobValue> mapKnobs) {
		this.mapKnobs = mapKnobs;
	}

	public void addKnobs(String key, KnobValue value) {
		this.mapKnobs.put(key, value);
	}

	public double getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(double executionTime) {
		this.executionTime = executionTime;
	}
}