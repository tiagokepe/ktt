package ufpr.inf.kepe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ufpr.inf.kepe.util.AutoConfBash;
import ufpr.inf.kepe.util.HadoopBash;
import ufpr.inf.kepe.util.Knob;
import ufpr.inf.kepe.util.Properties;

public abstract class AbstractAlg {
	protected static final int MAX_GENERATION = 100;
	protected static final double MIN_FITNESS = 0;
	protected static final double WORST_FITNESS = 999999999;
	protected static final String WHITOUT_SAMPLE = "0.0";
	
	protected List<Individual> populationIndividuals;
	protected Individual bestIndividual;
	protected int maxGeneration = MAX_GENERATION;
	protected double fitnessTarget = MIN_FITNESS;
	protected String jobName;
	protected File tmpFile;
	protected File resultFile;
	protected Properties properties;
	protected boolean alreadyAdded;
	protected int numIndvPerPop;
	
	public AbstractAlg(List<Individual> populationIndividuals) {
		this.populationIndividuals = populationIndividuals;
		this.properties = new Properties();
		this.alreadyAdded = false;
	}
	
	protected abstract Individual runAlg() throws InterruptedException, IOException;
	
	protected void mutation() {
		for (Individual indiv : populationIndividuals)
			indiv.autoMutation();
	}
	
	protected void createTmpDir()
	{
		this.tmpFile = new File("tmp/"+jobName);
		if(!tmpFile.exists())
		{
			tmpFile.mkdirs();
		}
	}
	
	protected void createResultDir() {
		this.resultFile = new File("result/"+jobName);
		if(!this.resultFile.exists())
		{
			this.resultFile.mkdirs();
		}
	}
	
	public Individual startAlg() throws InterruptedException, IOException
	{
		createTmpDir();
		createResultDir();
		return runAlg();
	}
	
	protected double calcFitiness(Individual individual) throws IOException, InterruptedException
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
	
	protected File createAutoConfFile(Individual indiv) throws IOException
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

		for (Map.Entry<String, Knob> entry : indiv.getMapKnobs().entrySet())
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
		String opts;
		if(this.getProperties().getSamplePercent().equals(WHITOUT_SAMPLE)) {
			opts = "jar " + properties.getJarPath() + " " 
					 + properties.getJobClassName() + " " 
					 + properties.getPathInputDirHDFS() + " "
					 + properties.getPathOutputDirHDFS() + "/" + strDate;			
		} else {
			opts = "jar " + properties.getJarPath() + " " 
					 + properties.getJobClassName() + " " 
					 + properties.getPathOutputDirHDFS() + "/sample "
					 + properties.getPathOutputDirHDFS() + "/" + strDate;
		}
		HadoopBash.execHadoop(opts);
	}

	protected Individual selectBestIndividual() {
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
	
	protected void writeIndividualToFile(Individual indiv, int numGen)
	{
		TuningByTesting.writeResult(indiv, resultFile.getAbsolutePath()+"/generation"+numGen);
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

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public int getNumIndvPerPop() {
		return numIndvPerPop;
	}

	public void setNumIndvPerPop(int numIndvPerPop) {
		this.numIndvPerPop = numIndvPerPop;
	}

	public Properties getProperties() {
		return properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public void readProperties(BufferedReader buffReader) throws IOException
	{
		this.properties.readProperties(buffReader);
	}
}
