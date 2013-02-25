package ufpr.inf.kepe;

import java.util.ArrayList;
import java.util.List;

import ufpr.inf.kepe.util.IntValue;

public class BacteriologicalAlgorithm 
{
    private List<Individual> populationIndividuals;
    private Individual bestIndividual;
    private int maxGeneration = MAX_GENERATION;
    private int numIndvPerPop = NUMBER_INDV_PER_POPULATION;
    private double fitnessTarget = MIN_FITNESS;
    
    private static final int MAX_GENERATION = 100;
    private static final int NUMBER_INDV_PER_POPULATION = 3;
    private static final double MIN_FITNESS = 0;
    private static final double WORST_FITNESS = 999999999;

    public BacteriologicalAlgorithm()
    {
        this.populationIndividuals = new ArrayList<Individual>();
    }
    
    /**
     * @author Tiago Kepe
     * @param populationIndividuals initial population of individuals. 
     */
    public BacteriologicalAlgorithm(List<Individual> populationIndividuals)
    {
        this.populationIndividuals = populationIndividuals;
    }
    
    public Individual startAlg() throws InterruptedException
    {
    	int numGenerations = 0;
    	do
    	{
    		for(Individual indiv: populationIndividuals)
    			calcFitiness(indiv);
    		
    		setBestIndividual(selectBestIndividual());
    		reproduction();
    		mutation();
    		numGenerations++;
    	} while ( (numGenerations < maxGeneration) && 
    			  (bestIndividual.getExecutionTime() > fitnessTarget) );
    		
    	return bestIndividual;
    }
    
    private double calcFitiness(Individual individual) throws InterruptedException
    {
    	final long startTime = System.currentTimeMillis();
        for(Object obj: individual.getMapKnobs().values())
        {
        	if(obj instanceof IntValue)
        		Thread.sleep((((IntValue)obj).getValue()));
        }
        
        double result = System.currentTimeMillis() - startTime;
        // Change for seconds
        result = result / 1000;
        individual.setExecutionTime(result);
        return result;
    }
    
    private Individual selectBestIndividual()
    {
    	double bestTime;
    	Individual bestIndiv;
    	if(this.bestIndividual != null)
    	{
    		bestTime = this.bestIndividual.getExecutionTime();
    		bestIndiv = this.bestIndividual;
    	}
    	else
    	{
    		bestTime = WORST_FITNESS;
    		bestIndiv = null;
    	}
    	for(Individual indiv: populationIndividuals)
    		if(bestTime > indiv.getExecutionTime())
    		{
    			bestTime = indiv.getExecutionTime();
    			bestIndiv = indiv;
    		}
    	
    	return bestIndiv;
    }
    
    private void reproduction()
    {
    	int i;
    	populationIndividuals.clear();
    	for(i=0; i<numIndvPerPop; i++)
    		populationIndividuals.add(bestIndividual.clone());
    }
    
    private void mutation()
    {
    	for(Individual indiv: populationIndividuals)
    		indiv.autoMutation();
    }

    
	public List<Individual> getPopulationIndividuals()
    {
        return populationIndividuals;
    }
	
    public void setPopulationIndividuals(List<Individual> populationIndividuals)
    {
        this.populationIndividuals = populationIndividuals;
    }
    
    public void addIndivToPoputlation(Individual indiv)
    {
    	this.populationIndividuals.add(indiv);
    }
    
    public Individual getBestIndividual()
    {
        return bestIndividual;
    }
    
    public void setBestIndividual(Individual bestIndividual)
    {
        this.bestIndividual = bestIndividual;
    }
    
    public int getMaxGeneration() {
		return maxGeneration;
	}

	public void setMaxGeneration(int maxGeneration) {
		// Limitation number of generation
		if(maxGeneration <= MAX_GENERATION)
			this.maxGeneration = maxGeneration;
	}

	public int getNumIndvPerPop() {
		return numIndvPerPop;
	}

	public void setNumIndvPerPop(int numIndvPerPop) {
		this.numIndvPerPop = numIndvPerPop;
	}
}
