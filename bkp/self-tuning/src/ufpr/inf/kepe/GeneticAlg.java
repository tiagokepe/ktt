package ufpr.inf.kepe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import ufpr.inf.kepe.util.Knob;

public class GeneticAlg extends AbstractAlg {
	private static final int NUMBER_INDV_PER_POPULATION = 4;
	
	public GeneticAlg() {
		this(new ArrayList<Individual>());
	}
	
	public GeneticAlg(List<Individual> populationIndividuals) {
		super(populationIndividuals);
		if(properties.getPopulationSize() == 0)
			this.numIndvPerPop = NUMBER_INDV_PER_POPULATION;
		else
			this.numIndvPerPop = properties.getPopulationSize();
	}

	@Override
	protected Individual runAlg() throws InterruptedException, IOException {
		this.setMaxGeneration();
		int numGenerations = 0;
		DateTime startDateTime, endDateTime;
		Seconds roundSeconds;
		do {
			System.out.println("New generation "+numGenerations);
			System.out.println("Calculing fitness...");
			startDateTime = new DateTime();
			for (Individual indiv : populationIndividuals)
				calcFitiness(indiv);
			
			setBestIndividual(selectBestIndividual());
			writeIndividualToFile(getBestIndividual(), numGenerations);
			System.out.println("Selected the best individual.");
			populationIndividuals = reproduction();
			System.out.println("Created new population.");
			crossover();
			System.out.println("Crossover individuals.");
			mutation();
			endDateTime = new DateTime();
			System.out.println("Mutated individuals.");
			numGenerations++;
			roundSeconds = Seconds.secondsBetween(startDateTime, endDateTime);
			System.out.println("Round time: "+roundSeconds.getSeconds()+"s.");
			System.out.println("--------------------------------------------------");
		} while ((numGenerations < maxGeneration)
				&& (bestIndividual.getExecutionTime() > fitnessTarget));
		
		return bestIndividual;
		
	}

	private List<Individual> reproduction() {
		SortedMap<Double, Individual> bestIndividuals = new TreeMap<Double, Individual>();
		for(Individual indiv: populationIndividuals)
		{
			if(bestIndividuals.size() < numIndvPerPop)
				bestIndividuals.put(indiv.getExecutionTime(), indiv);
			else {
				insertIndividualOrdered(bestIndividuals, indiv);
			}
		}
		
		double sumExecutionTime = 0;
		for(Double time: bestIndividuals.keySet()) 
			sumExecutionTime += time;

		List<Double> invertedScores = new ArrayList<Double>();
		double sumInverted = 0;
		double newScore;
		for(SortedMap.Entry<Double, Individual> entry: bestIndividuals.entrySet()) {
			newScore = sumExecutionTime - entry.getKey();
			invertedScores.add(newScore);
			sumInverted += newScore;
		}
		
		Roulette roulette = new Roulette(numIndvPerPop*2);
		Object[] keyArray = bestIndividuals.keySet().toArray();
		Double probability;
		for(int i=0; i<keyArray.length; i++) {
			probability = invertedScores.get(i) / sumInverted;
			roulette.insertRange(i, probability, bestIndividuals.get(keyArray[i]));
		}
		
		return roulette.spin();
	}
	
	private void insertIndividualOrdered(SortedMap<Double, Individual> map, Individual indiv) {
		Individual currentIndiv;
		
		for(SortedMap.Entry<Double, Individual> entry: map.entrySet()) {
			currentIndiv = entry.getValue();
			if(currentIndiv.getExecutionTime() > indiv.getExecutionTime()) {
				map.remove(map.lastKey());
				map.put(indiv.getExecutionTime(), indiv);
				break;
			}
			else if(currentIndiv.getExecutionTime() == indiv.getExecutionTime())
				break;
		}
	}
	
	private void crossover() {
		Individual indivA, indivB;
		List<Individual> newPop = new ArrayList<Individual>();
		for(int i=0; i< this.numIndvPerPop; i++) {
			indivA = this.raffleIndividual();
			indivB = this.raffleIndividual();
			newPop.add(this.crossoverIndividuals(indivA, indivB));
		}
		populationIndividuals = newPop;
	}
	
	private Individual raffleIndividual() {
		int currentPopSize = populationIndividuals.size();
		Random rand = new Random();
		int posIndiv = rand.nextInt(currentPopSize);
		Individual chosenIndividual = populationIndividuals.get(posIndiv);
		populationIndividuals.remove(posIndiv);
		return chosenIndividual;
		
	}
	
	private Individual crossoverIndividuals(Individual indivA, Individual indivB) {
		Individual newIndiv = new Individual();
		int totalKnobs = (indivA.getMapKnobs().size() > indivB.getMapKnobs().size())
						? indivA.getMapKnobs().size() : indivB.getMapKnobs().size();
		Random rand = new Random();
		int k = rand.nextInt(totalKnobs);
		String key;
		Knob value;
		for(int i=0; i < k; i++)
		{
			key = indivA.getMapKnobs().firstKey();
			value = indivA.getMapKnobs().get(key);
			newIndiv.getMapKnobs().put(key, value);
			indivA.getMapKnobs().remove(key);
			
			//Remove the k first elements of the IndivB
			indivB.getMapKnobs().remove(indivB.getMapKnobs().firstKey());
		}
		
		Iterator<Entry<String, Knob>> it = indivB.getMapKnobs().entrySet().iterator();
		Entry<String, Knob> entry;
		while(it.hasNext())
		{
			entry = it.next();
			newIndiv.getMapKnobs().put(entry.getKey(), entry.getValue());
		}
		
		return newIndiv;
	}
}