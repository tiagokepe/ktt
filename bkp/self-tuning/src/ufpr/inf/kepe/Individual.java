package ufpr.inf.kepe;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import ufpr.inf.kepe.util.Knob;
import ufpr.inf.kepe.util.KnobBoolean;
import ufpr.inf.kepe.util.KnobFloat;
import ufpr.inf.kepe.util.KnobInt;
import ufpr.inf.kepe.util.KnobValue;

public class Individual {
	private SortedMap<String, Knob> mapKnobs;
	private double executionTime;

	public Individual() {
		this.mapKnobs = new TreeMap<String, Knob>();
	}

	public Individual(SortedMap<String, Knob> mapKnobs) {
		this.mapKnobs = mapKnobs;
	}

	public void autoMutation() {
		String[] keySet = (String[]) mapKnobs.keySet().toArray(new String[0]);
		Random rand = new Random();
		int posKey = rand.nextInt(keySet.length);
		String keyMutate = keySet[posKey];
		Knob value = mapKnobs.get(keyMutate);

		if (value instanceof KnobInt)
		{
			rand = new Random();
			int major = ((KnobInt) value).getMax();
			int valueMutate;
			do {
				valueMutate = rand.nextInt(major);
			} while(valueMutate < ((KnobInt) value).getMin());
			
			((KnobInt) value).setValue(valueMutate);
			mapKnobs.put(keyMutate, value);
		}
		else if (value instanceof KnobFloat)
		{
			rand = new Random();
			Float valueMutate = rand.nextFloat();
			((KnobFloat) value).setValue(valueMutate);
			mapKnobs.put(keyMutate, value);
		}
		else if (value instanceof KnobBoolean) {
			rand = new Random();
			Boolean valueMutate = rand.nextBoolean();
			((KnobBoolean) value).setValue(valueMutate);
			mapKnobs.put(keyMutate, value);
		}
	}

	public Individual clone() {
		Individual indivCloned = new Individual();
		SortedMap<String, Knob> copyKnobs = new TreeMap<String, Knob>();
		String key;
		Object oldValue;
		for (Map.Entry<String, Knob> entry : this.mapKnobs.entrySet()) {
			key = entry.getKey();
			oldValue = entry.getValue();
			if (oldValue instanceof KnobInt)
			{
				KnobInt value = new KnobInt();
				value.setValue(((KnobInt) oldValue).getValue());
				value.setMax(((KnobInt) oldValue).getMax());
				value.setMin(((KnobInt) oldValue).getMin());

				copyKnobs.put(key, value);
			}
			else if (oldValue instanceof KnobFloat)
			{
				KnobFloat value = new KnobFloat();
				value.setValue(((KnobFloat) oldValue).getValue());
				value.setMax(((KnobFloat) oldValue).getMax());
				value.setMin(((KnobFloat) oldValue).getMin());
				
				copyKnobs.put(key, value);
			}
			else {
				KnobBoolean value = new KnobBoolean();
				value.setValue(((KnobBoolean) oldValue).getValue());
				copyKnobs.put(key, value);
			}
		}

		indivCloned.setMapKnobs(copyKnobs);
		return indivCloned;
	}

	public SortedMap<String, Knob> getMapKnobs() {
		return mapKnobs;
	}

	public void setMapKnobs(SortedMap<String, Knob> mapKnobs) {
		this.mapKnobs = mapKnobs;
	}

	public void addKnobs(String key, Knob value) {
		this.mapKnobs.put(key, value);
	}

	public double getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(double executionTime) {
		this.executionTime = executionTime;
	}
}