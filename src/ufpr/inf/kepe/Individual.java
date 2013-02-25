package ufpr.inf.kepe;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ufpr.inf.kepe.util.IntValue;

public class Individual
{
	private Map<String, Object> mapKnobs;
	private double executionTime;
	
	public Individual()
	{
		this.mapKnobs = new HashMap<String, Object>();
	}
	
	public Individual(Map<String, Object> mapKnobs)
	{
		this.mapKnobs = mapKnobs;
	}
	
	public void autoMutation()
	{
		String[] keySet = (String[])mapKnobs.keySet().toArray(new String[0]);
		Random rand = new Random();
		int posKey = rand.nextInt(keySet.length);
		String keyMutate = keySet[posKey];
		Object value = mapKnobs.get(keyMutate);
		
		if(value instanceof IntValue)
		{
			rand = new Random();
			int major = ((IntValue) value).getMax();
			int valueMutate = rand.nextInt(major);
			((IntValue) value).setValue(valueMutate);
			mapKnobs.put(keyMutate, value);
		}
		else if(value instanceof Float)
		{
			rand = new Random();
			Float valueMutate = rand.nextFloat();
			mapKnobs.put(keyMutate, valueMutate);
		}
		else if(value instanceof Boolean)
		{
			rand = new Random();
			Boolean valueMutate = rand.nextBoolean();
			mapKnobs.put(keyMutate, valueMutate);
		}
	}
	
	public Individual clone()
	{
		Individual indivCloned = new Individual();
		Map<String, Object> copyKnobs = new HashMap<String, Object>();
		String key;
		Object oldValue;
		for (Map.Entry<String, Object> entry: this.mapKnobs.entrySet())
		{
			key = entry.getKey();
			oldValue = entry.getValue();
			if(oldValue instanceof IntValue)
			{
				IntValue value = new IntValue();
				value.setValue( ((IntValue)oldValue).getValue() );
				value.setMax( ((IntValue)oldValue).getMax() );
				
				copyKnobs.put(key, value);
			}
			else if(oldValue instanceof Float)
			{
				Float value = new Float((Float)oldValue);
				copyKnobs.put(key, value);
			}
			else
			{
				Boolean value = new Boolean((Boolean)oldValue);
				copyKnobs.put(key, value);
			}
		}
			
		indivCloned.setMapKnobs(copyKnobs);
		return indivCloned;
	}

    public Map<String, Object> getMapKnobs()
    {
        return mapKnobs;
    }

    public void setMapKnobs(Map<String, Object> mapKnobs)
    {
        this.mapKnobs = mapKnobs;
    }
    
    public void addKnobs(String key, Object value)
    {
    	this.mapKnobs.put(key, value);
    }

    public double getExecutionTime()
    {
        return executionTime;
    }

    public void setExecutionTime(double executionTime)
    {
        this.executionTime = executionTime;
    }
}
