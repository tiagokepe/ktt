package ufpr.inf.kepe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

public class BacteriologicalAlg extends AbstractAlg {
	private static final int NUMBER_INDV_PER_POPULATION = 3;

	public BacteriologicalAlg() {
		this(new ArrayList<Individual>());
	}

	/**
	 * @author Tiago Kepe
	 * @param populationIndividuals
	 *            initial population of individuals.
	 */
	public BacteriologicalAlg(List<Individual> populationIndividuals) {
		super(populationIndividuals);
		if(properties.getPopulationSize() == 0)
			this.numIndvPerPop = NUMBER_INDV_PER_POPULATION;
		else
			this.numIndvPerPop = properties.getPopulationSize();
	}

	public Individual startAlg() throws InterruptedException, IOException
	{
		createTmpDir();
		createResultDir();
		return runAlg();
	}
	

	protected Individual runAlg() throws InterruptedException, IOException
	{
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
			reproduction();
			System.out.println("Reproduced the best individual.");
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

	private void reproduction() {
		int i;
		populationIndividuals.clear();
		for (i = 0; i < numIndvPerPop; i++)
			populationIndividuals.add(bestIndividual.clone());
	}

	public int getNumIndvPerPop() {
		return numIndvPerPop;
	}

	public void setNumIndvPerPop(int numIndvPerPop) {
		this.numIndvPerPop = numIndvPerPop;
	}
}