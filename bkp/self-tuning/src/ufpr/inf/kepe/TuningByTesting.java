package ufpr.inf.kepe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import ufpr.inf.kepe.util.AutoConfBash;
import ufpr.inf.kepe.util.HadoopBash;
import ufpr.inf.kepe.util.Knob;
import ufpr.inf.kepe.util.KnobBoolean;
import ufpr.inf.kepe.util.KnobFloat;
import ufpr.inf.kepe.util.KnobInt;
import ufpr.inf.kepe.util.Properties;

public class TuningByTesting {

	private AbstractAlg algorithm;
	private static String jobName;
	private AutoConfBash autoConfBash;
	private File inputFile;
	//private String samplePercent;
	private Properties properties = new Properties();
	
	public TuningByTesting() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TuningByTesting tunTest = new TuningByTesting();
		tunTest.readArgs(args);
		try {
			tunTest.startAutoConf();
			tunTest.readPropertiesAndJobName();
			tunTest.cretateInitialPopulation();
			tunTest.sampling();
			Individual result = tunTest.start();
			tunTest.writeResult(result);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			tunTest.stopAutoConf();
		}
	}
	
	private void readArgs(String[] args)
	{
		if(args.length !=1)
		{
			System.out.println("Usage:");
			System.out.println("TuningByTest <.job file>");
			System.exit(-1);
		}
		this.inputFile = new File(args[0]);
	}
	
	private void sampling() throws IOException, InterruptedException
	{
		//this.samplePercent = algorithm.getProperties().getSamplePercent();
		if(!algorithm.getProperties().getSamplePercent().equals("0.0"))
		{
			HadoopBash.execHadoop("fs -rmr "+algorithm.getProperties().getPathOutputDirHDFS() + "/sample");
		
			System.out.println("Generating sample...");
			String sampleJarPath = System.getenv("KEPE_SAMPLE_HOME")+"/jar/sample-random.jar";
			String opts = "jar " + sampleJarPath + " " 
								 + algorithm.getProperties().getSamplePercent() + " " 
								 + algorithm.getProperties().getPathInputDirHDFS() + " "
								 + algorithm.getProperties().getPathOutputDirHDFS() + "/sample";
			
			HadoopBash.execHadoop(opts);
			System.out.println("Sample generated!");
			System.out.println("--------------------------------------------------");
		}
	}
	
	private int startAutoConf() throws IOException, InterruptedException {
		this.autoConfBash = new AutoConfBash();
		return autoConfBash.runAutoConfServer();
	}
	
	private void stopAutoConf()
	{
		autoConfBash.stopAutoConfServer();
	}
	
	private void readPropertiesAndJobName() {
		BufferedReader buffReader = null;

		try {
			buffReader = new BufferedReader(new FileReader(this.inputFile));
		} catch (FileNotFoundException ex) {
			System.out.println("File not found: " + this.inputFile.getAbsolutePath());
			System.exit(1);
		}

		String line;
		try {
			while (buffReader.ready()) {
				line = buffReader.readLine();
				if (line.contains("Job"))
				{
					this.jobName = line.split(" ")[1];
				}
				else if(line.contains("Properties"))
				{
					this.properties.readProperties(buffReader);
					this.setAlgorithm();
					this.setAlgProperties();
				}
			}
		} catch (IOException ex) {
			System.out.println("Error to read the file: "
					+ this.inputFile.getAbsolutePath());
			System.exit(2);
		}

		try {
			buffReader.close();
		} catch (IOException e) {
			System.out.println("Error to close the file: "
					+ this.inputFile.getAbsolutePath());
			System.exit(2);
		}
	}


	private void setAlgorithm() {
		if(this.properties.getAlgorithm().equals(AlgorithmType.Bacteriological.getAlgName()))
			this.algorithm = new BacteriologicalAlg();
		else
			this.algorithm = new GeneticAlg();
	}

	private void cretateInitialPopulation() {
		BufferedReader buffReader = null;

		try {
			buffReader = new BufferedReader(new FileReader(this.inputFile));
		} catch (FileNotFoundException ex) {
			System.out.println("File not found: " + this.inputFile.getAbsolutePath());
			System.exit(1);
		}

		String line;
		try {
			while (buffReader.ready()) {
				line = buffReader.readLine();
				if (line.contains("Knobs"))
				{
					this.algorithm.addIndivToPoputlation(readKnobs(buffReader));
				}	
			}
		} catch (IOException ex) {
			System.out.println("Error to read the file: "
					+ this.inputFile.getAbsolutePath());
			System.exit(2);
		}

		try {
			buffReader.close();
		} catch (IOException e) {
			System.out.println("Error to close the file: "
					+ this.inputFile.getAbsolutePath());
			System.exit(2);
		}
	}

	private void setAlgProperties() {
		this.algorithm.setProperties(this.properties);
	}
	
	private Individual start() throws InterruptedException, IOException {
		algorithm.setJobName(jobName);
		return algorithm.startAlg();
	}

	private Individual readKnobs(BufferedReader buffReader) throws IOException {
		Individual indiv = new Individual();
		String line;
		String token;
		String key = null;
		Knob value = null;
		do {
			try {
				line = buffReader.readLine();
			} catch (IOException ex) {
				throw ex;
			}

			StringTokenizer strTokens = new StringTokenizer(line);
			while (strTokens.hasMoreTokens()) {
				token = strTokens.nextToken(" ");
				if (token.contains("int"))
				{
					key = strTokens.nextToken(" ");

					value = new KnobInt();

					token = strTokens.nextToken(" ");
					((KnobInt) value).setMin(Integer.parseInt(token));

					token = strTokens.nextToken(" ");
					((KnobInt) value).setMax(Integer.parseInt(token));
					
					token = strTokens.nextToken(" = ");
					((KnobInt) value).setValue(Integer.parseInt(token));
					
					indiv.addKnobs(key, value);
				}
				if (token.contains("float"))
				{
					key = strTokens.nextToken(" ");

					value = new KnobFloat();

					token = strTokens.nextToken(" ");
					((KnobFloat) value).setMin(Float.parseFloat(token));

					token = strTokens.nextToken(" ");
					((KnobFloat) value).setMax(Float.parseFloat(token));
					
					token = strTokens.nextToken(" = ");
					((KnobFloat) value).setValue(Float.parseFloat(token));
					
					indiv.addKnobs(key, value);
				}
				if(token.contains("boolean"))
				{
					key = strTokens.nextToken(" ");
					
					value = new KnobBoolean();
					
					((KnobBoolean) value).setValue(Boolean.parseBoolean(token));
				}
			}
		} while (!line.contains("}"));

		return indiv;
	}
	
	private void writeResult(Individual bestIndiv)
	{
		Date date = new Date();
		String path = "result/" + jobName + "_" + date.toString();
		writeResult(bestIndiv, path);
	}
	
	public static void writeResult(Individual bestIndiv, String pathFile)
	{
		File fileResult = new File(pathFile);
		BufferedWriter buffWriter = null;

		try {
			buffWriter = new BufferedWriter(new FileWriter(fileResult));
		} catch (IOException e) {
			System.out.println("Error to open result file: "
					+ fileResult.getAbsolutePath());
			System.exit(1);
		}

		Knob value = null;
		try {
			buffWriter.write("Job " + jobName + " {\n");
			buffWriter.write("\tKnobs {\n");
			for (Map.Entry<String, Knob> entry : bestIndiv.getMapKnobs().entrySet()) {
				if(entry.getValue() instanceof KnobInt) {
					value = (KnobInt) entry.getValue();
					buffWriter.write("\t\t"
							+ value.type() + " "
							+ entry.getKey() + " "
							+ ((KnobInt)value).getMin() + " "
							+ ((KnobInt)value).getMax() + " = "
							+ value.getValue() + "\n");
				} else if(entry.getValue() instanceof KnobFloat) {
					value = (KnobFloat) entry.getValue();
					buffWriter.write("\t\t"
							+ value.type() + " "
							+ entry.getKey() + " "
							+ ((KnobFloat)value).getMin() + " "
							+ ((KnobFloat)value).getMax() + " = "
							+ value.getValue() + "\n");
				} else if(entry.getValue() instanceof KnobBoolean) {
					value = (KnobBoolean) entry.getValue();
					buffWriter.write("\t\t"
							+ value.type() + " "
							+ entry.getKey() + " = "
							+ value.getValue() + "\n");
				}
			}
			buffWriter.write("\t}\n");
			buffWriter.write("\t{Execution time: "+bestIndiv.getExecutionTime()+"}\n");
			buffWriter.write("}\n");
		} catch (IOException e) {
			System.out.println("Error to write in the result file: "
					+ fileResult.getAbsolutePath());
			System.exit(3);
		}

		try {
			buffWriter.close();
		} catch (IOException e) {
			System.out.println("Error to close the file: "
					+ fileResult.getAbsolutePath());
			System.exit(2);
		}
	}
}