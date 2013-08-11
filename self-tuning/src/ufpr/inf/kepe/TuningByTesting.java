package ufpr.inf.kepe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import ufpr.inf.kepe.util.AutoConfBash;
import ufpr.inf.kepe.util.FloatValue;
import ufpr.inf.kepe.util.HadoopBash;
import ufpr.inf.kepe.util.IntValue;
import ufpr.inf.kepe.util.KnobValue;

public class TuningByTesting {

	private BacteriologicalAlgorithm bactAlg;
	private static String jobName;
	private AutoConfBash autoConfBash;
	private File inputFile;
	private String samplePercent;
	
	public TuningByTesting() {
		bactAlg = new BacteriologicalAlgorithm();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TuningByTesting tunTest = new TuningByTesting();
		tunTest.readArgs(args);
		try {
			tunTest.startAutoConf();
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
	
	public String getSamplePercent() {
		return samplePercent;
	}

	private void readArgs(String[] args)
	{
		if(args.length != 2)
		{
			System.out.println("Usage:");
			System.out.println("TuningByTest <file with the initial population>"
							+ " <.job file>"
							+ " <sample percent>");
			System.exit(-1);
		}
		this.inputFile = new File(args[0]);
		this.samplePercent = args[1];
	}
	
	private void sampling() throws IOException, InterruptedException
	{
		if(!this.samplePercent.equals("0.0"))
		{
			HadoopBash.execHadoop("fs -rmr "+bactAlg.getProperties().getPathOutputDirHDFS() + "/sample");
		
			System.out.println("Generating sample...");
			String sampleJarPath = System.getenv("KEPE_SAMPLE_HOME")+"/jar/sample-random.jar";
			String opts = "jar " + sampleJarPath + " " 
								 + this.samplePercent + " " 
								 + bactAlg.getProperties().getPathInputDirHDFS() + " "
								 + bactAlg.getProperties().getPathOutputDirHDFS() + "/sample";
			
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
				if (line.contains("Job"))
				{
					this.jobName = line.split(" ")[1];
				}
				else if(line.contains("Properties"))
				{
					this.bactAlg.readProperties(buffReader);
				}
				else if (line.contains("Knobs"))
				{
					this.bactAlg.addIndivToPoputlation(readKnobs(buffReader));
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

	private Individual start() throws InterruptedException, IOException {
		bactAlg.setJobName(jobName);
		return bactAlg.startAlg();
	}

	private Individual readKnobs(BufferedReader buffReader) throws IOException {
		Individual indiv = new Individual();
		String line;
		String token;
		String key = null;
		KnobValue value = null;
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

					value = new IntValue();

					token = strTokens.nextToken(" ");
					((IntValue) value).setMin(Integer.parseInt(token));

					token = strTokens.nextToken(" ");
					((IntValue) value).setMax(Integer.parseInt(token));
					
					token = strTokens.nextToken(" = ");
					((IntValue) value).setValue(Integer.parseInt(token));
					
					indiv.addKnobs(key, value);
				}
				if (token.contains("float"))
				{
					key = strTokens.nextToken(" ");

					value = new FloatValue();

					token = strTokens.nextToken(" ");
					((FloatValue) value).setMin(Float.parseFloat(token));

					token = strTokens.nextToken(" ");
					((FloatValue) value).setMax(Float.parseFloat(token));
					
					token = strTokens.nextToken(" = ");
					((FloatValue) value).setValue(Float.parseFloat(token));
					
					indiv.addKnobs(key, value);
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

		try {
			buffWriter.write("Job " + jobName + " {\n");
			buffWriter.write("\tKnobs {\n");
			for (Map.Entry<String, KnobValue> entry : bestIndiv.getMapKnobs().entrySet())
				buffWriter.write("\t\t"
						+ entry.getValue().type() + " "
						+ entry.getKey() + " "
						+ entry.getValue().getMin() + " "
						+ entry.getValue().getMax() + " = "
						+ entry.getValue().getValue() + "\n");

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