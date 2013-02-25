package ufpr.inf.kepe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import ufpr.inf.kepe.util.IntValue;

public class TuningByTesting
{

	private BacteriologicalAlgorithm bactAlg;
	private String jobName;
	
	public TuningByTesting()
	{
		bactAlg = new BacteriologicalAlgorithm();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		TuningByTesting tunTest = new TuningByTesting();
		tunTest.cretateInitialPopulation();
		tunTest.bactAlg.setMaxGeneration(3);
		try {
			Individual result = tunTest.start();
			tunTest.writeResult(result);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void cretateInitialPopulation()
	{
		File input = new File("input/jobTime");
		BufferedReader buffReader = null;
		
		try
		{
			buffReader = new BufferedReader(new FileReader(input));
		}
		catch (FileNotFoundException ex)
		{
			System.out.println("File not found: "+input.getAbsolutePath());
			System.exit(1);
		}
		
		String line;
		try
		{
			while(buffReader.ready())			
			{	
				line = buffReader.readLine();
				if(line.contains("Job"))
				{
					this.jobName = line.split(" ")[1];
				}
				else if(line.contains("Knobs"))
				{
					this.bactAlg.addIndivToPoputlation(readKnobs(buffReader));
				}
			}
		}
		catch(IOException ex)
		{
			System.out.println("Error to read the file: "+input.getAbsolutePath());
			System.exit(2);
		}
		
		try {
			buffReader.close();
		} catch (IOException e) {
			System.out.println("Error to close the file: "+input.getAbsolutePath());
			System.exit(2);
		}
	}
	
	private Individual start() throws InterruptedException
	{
		return bactAlg.startAlg();
	}
	
	private Individual readKnobs(BufferedReader buffReader) throws IOException
	{
		Individual indiv = new Individual();
		String line;
		String token;
		String key = null;
		Object value = null;
		do
		{
			try {
				line = buffReader.readLine();
			} catch (IOException ex) {
				throw ex;
			}
			
			StringTokenizer strTokens = new StringTokenizer(line);			
			while(strTokens.hasMoreTokens())
			{
				token = strTokens.nextToken(" ");
				if(token.contains("int"))
				{
					key = strTokens.nextToken(" ");
					
					value = new IntValue();
					
					token = strTokens.nextToken(" ");
					((IntValue)value).setValue(Integer.parseInt(token));

					token = strTokens.nextToken(" ");
					((IntValue)value).setMax(Integer.parseInt(token));
				}
				indiv.addKnobs(key, value);
			}
		} while (!line.contains("}")); 
		
		return indiv;
	}
	
	private void writeResult(Individual bestIndiv)
	{
		Date date = new Date();
		File fileResult = new File("result/" + this.jobName + "_" + date.toString());
		BufferedWriter buffWriter = null;
		
		try
		{
			buffWriter = new BufferedWriter(new FileWriter(fileResult));
		}
		catch (IOException e)
		{
			System.out.println("Error to open result file: "+fileResult.getAbsolutePath());
			System.exit(1);
		}
		
		try {
			buffWriter.write("Job "+this.jobName+" {\n");
			buffWriter.write("\tKnobs {\n" );
			for (Map.Entry<String, Object> entry: bestIndiv.getMapKnobs().entrySet())
				buffWriter.write("\t\t"+entry.getKey()+" "+((IntValue)entry.getValue()).getValue()+"\n");

			buffWriter.write("\t}\n" );
			buffWriter.write("}\n" );
		} catch (IOException e) {
			System.out.println("Error to write in the result file: "+fileResult.getAbsolutePath());
			System.exit(3);
		}
		
		try {
			buffWriter.close();
		} catch (IOException e) {
			System.out.println("Error to close the file: "+fileResult.getAbsolutePath());
			System.exit(2);
		}
		
	}
}
