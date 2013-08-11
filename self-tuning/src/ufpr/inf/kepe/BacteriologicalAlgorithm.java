package ufpr.inf.kepe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ufpr.inf.kepe.util.AutoConfBash;
import ufpr.inf.kepe.util.HadoopBash;
import ufpr.inf.kepe.util.Properties;
import ufpr.inf.kepe.util.KnobValue;

public class BacteriologicalAlgorithm {
	private List<Individual> populationIndividuals;
	private Individual bestIndividual;
	private int maxGeneration = MAX_GENERATION;
	private int numIndvPerPop = NUMBER_INDV_PER_POPULATION;
	private double fitnessTarget = MIN_FITNESS;
	private String jobName;
	private File tmpFile;
	private Properties properties;
	private boolean alreadyAdded;

	private static final int MAX_GENERATION = 100;
	private static final int NUMBER_INDV_PER_POPULATION = 3;
	private static final double MIN_FITNESS = 0;
	private static final double WORST_FITNESS = 999999999;

	public BacteriologicalAlgorithm() {
		this(new ArrayList<Individual>());
	}

	/**
	 * @author Tiago Kepe
	 * @param populationIndividuals
	 *            initial population of individuals.
	 */
	public BacteriologicalAlgorithm(List<Individual> populationIndividuals) {
		this.populationIndividuals = populationIndividuals;
		this.properties = new Properties();
		this.alreadyAdded = false;
	}

	public Individual startAlg() throws InterruptedException, IOException
	{
		createTmpDir();
		return runAlg();
	}
	
	private void createTmpDir()
	{
		this.tmpFile = new File("tmp/"+jobName);
		if(!tmpFile.exists())
		{
			tmpFile.mkdirs();
		}
	}
	
	private Individual runAlg() throws InterruptedException, IOException
	{
		this.setMaxGeneration();
		int numGenerations = 0;
		do {
			System.out.println("New generation "+numGenerations);
			System.out.println("Calculing fitness...");
			for (Individual indiv : populationIndividuals)
				calcFitiness(indiv);

			setBestIndividual(selectBestIndividual());
			writeIndividualToFile(getBestIndividual(), numGenerations);
			System.out.println("Selected the best individual.");
			reproduction();
			System.out.println("Reproduced the best individual.");
			mutation();
			System.out.println("Mutated individuals.");
			numGenerations++;
			System.out.println("--------------------------------------------------");
		} while ((numGenerations < maxGeneration)
				&& (bestIndividual.getExecutionTime() > fitnessTarget));

		return bestIndividual;		
	}

	private double calcFitiness(Individual individual) throws IOException, InterruptedException
	{
		File confFile = createAutoConfFile(individual);
		setConfigToAutoConf(confFile);
		final long startTime = System.currentTimeMillis();
		execHadoop();
		double result = System.currentTimeMillis() - startTime;
		// Change for seconds
		result = result / 1000;
		individual.setExecutionTime(result);
		return result;
	}
	
	private File createAutoConfFile(Individual indiv) throws IOException
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyy-mm-dd_hh:mm:ss");
		String strDate = dateFormat.format(new Date());
		File confFile = new File(tmpFile.getAbsolutePath() + "/" + strDate + "_" + indiv.toString() +".conf");

		BufferedWriter buffWriter = null;

		try {
			buffWriter = new BufferedWriter(new FileWriter(confFile));
		} catch (IOException e) {
			throw new IOException("Error to open conf file: "+ confFile.getAbsolutePath());
		}

		for (Map.Entry<String, KnobValue> entry : indiv.getMapKnobs().entrySet())
			try {
				buffWriter.write(entry.getKey() + " = "	
								+ entry.getValue().getValue()+"\n");
			} catch (IOException e) {
				buffWriter.close();
				throw new IOException("Error to write in the conf file: "
						+ confFile.getAbsolutePath());
			}
		
		try {
			buffWriter.close();
		} catch (IOException e) {
			throw new IOException("Error to close the file: " + confFile.getAbsolutePath());
		}
		return confFile;
	}
	
	public void setConfigToAutoConf(File confFile) throws IOException, InterruptedException
	{
		String opts;
		if(alreadyAdded)
			opts = "-j " + jobName + " -f " + confFile.getAbsolutePath() + " -u";
		else
		{
			opts = "-j " + jobName + " -f " + confFile.getAbsolutePath() + " -a";
			this.alreadyAdded = true;
		}

		AutoConfBash.execAutoConfCtl(opts);
	}
	
	public void execHadoop() throws IOException, InterruptedException
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyy-mm-dd_hh_mm_ss");
		String strDate = dateFormat.format(new Date());
		//TODO tem que apagar o diretório de saída ou criar um novo diretório
		String opts = "jar " + properties.getJarPath() + " " 
							 + properties.getJobClassName() + " " 
							 /*+ properties.getPathInputDirHDFS() + " "*/
							 + properties.getPathOutputDirHDFS() + "/sample "
							 + properties.getPathOutputDirHDFS() + "/" + strDate;
		HadoopBash.execHadoop(opts);
	}

	private Individual selectBestIndividual() {
		double bestTime;
		Individual bestIndiv;
		if (this.bestIndividual != null) {
			bestTime = this.bestIndividual.getExecutionTime();
			bestIndiv = this.bestIndividual;
		} else {
			bestTime = WORST_FITNESS;
			bestIndiv = null;
		}
		for (Individual indiv : populationIndividuals)
			if (bestTime > indiv.getExecutionTime()) {
				bestTime = indiv.getExecutionTime();
				bestIndiv = indiv;
			}

		return bestIndiv;
	}

	private void reproduction() {
		int i;
		populationIndividuals.clear();
		for (i = 0; i < numIndvPerPop; i++)
			populationIndividuals.add(bestIndividual.clone());
	}

	private void mutation() {
		for (Individual indiv : populationIndividuals)
			indiv.autoMutation();
	}
	
	private void writeIndividualToFile(Individual indiv, int numGen)
	{
		TuningByTesting.writeResult(indiv, "result/"+jobName+"/generation"+numGen);
	}
	
	public void readProperties(BufferedReader buffReader) throws IOException
	{
		this.properties.readProperties(buffReader);
	}

	public Properties getProperties() {
		return properties;
	}

	public List<Individual> getPopulationIndividuals() {
		return populationIndividuals;
	}

	public void setPopulationIndividuals(List<Individual> populationIndividuals) {
		this.populationIndividuals = populationIndividuals;
	}

	public void addIndivToPoputlation(Individual indiv) {
		this.populationIndividuals.add(indiv);
	}

	public Individual getBestIndividual() {
		return bestIndividual;
	}

	public void setBestIndividual(Individual bestIndividual) {
		this.bestIndividual = bestIndividual;
	}

	public int getMaxGeneration() {
		return maxGeneration;
	}

	public void setMaxGeneration() {
		// Limitation number of generation
		this.maxGeneration = properties.getNumGenerations();
	}

	public int getNumIndvPerPop() {
		return numIndvPerPop;
	}

	public void setNumIndvPerPop(int numIndvPerPop) {
		this.numIndvPerPop = numIndvPerPop;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
}