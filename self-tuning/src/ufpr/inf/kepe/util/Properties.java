package ufpr.inf.kepe.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import ufpr.inf.kepe.AlgorithmType;

public class Properties
{
	private String jarPath;
	private String jobClassName;
	private String pathInputDirHDFS;
	private String pathOutputDirHDFS;
	private int numGenerations;
	private String samplePercent;
	private AlgorithmType algorithm;
	private int populationSize;
	
	public String getJarPath() {
		return jarPath;
	}
	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}
	public String getJobClassName() {
		return jobClassName;
	}
	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}
	public String getPathInputDirHDFS() {
		return pathInputDirHDFS;
	}
	public void setPathInputDirHDFS(String pathInputDirHDFS) {
		this.pathInputDirHDFS = pathInputDirHDFS;
	}
	public String getPathOutputDirHDFS() {
		return pathOutputDirHDFS;
	}
	public void setPathOutputDirHDFS(String pathOutputDirHDFS) {
		this.pathOutputDirHDFS = pathOutputDirHDFS;
	}
	
	public int getNumGenerations() {
		return numGenerations;
	}
	public void setNumGenerations(int numGenerations) {
		this.numGenerations = numGenerations;
	}
	public String getSamplePercent() {
		return samplePercent;
	}
	public void setSamplePercent(String samplePercent) {
		this.samplePercent = samplePercent;
	}
	public int getPopulationSize() {
		return populationSize;
	}
	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}
	public AlgorithmType getAlgorithm() {
		return algorithm;
	}
	public void readProperties(BufferedReader buffReader) throws IOException {
		String line;
		String property;
		String value;;
		do {
			try {
				line = buffReader.readLine();
			} catch (IOException ex) {
				throw ex;
			}

			StringTokenizer strTokens = new StringTokenizer(line);
			while (strTokens.hasMoreTokens()) {
				property = strTokens.nextToken(" ");
				if (property.contains("jarPath"))
				{
					value = strTokens.nextToken(" ");
					this.jarPath = value;
				}
				else if (property.contains("jobClassName"))
				{
					value = strTokens.nextToken(" ");
					this.jobClassName = value;
				}
				else if (property.contains("pathInputDirHDFS"))
				{
					value = strTokens.nextToken(" ");
					this.pathInputDirHDFS = value;
				}
				else if (property.contains("pathOutputDirHDFS"))
				{
					value = strTokens.nextToken(" ");
					this.pathOutputDirHDFS = value;
				}
				else if (property.contains("numGenerations"))
				{
					value = strTokens.nextToken(" ");
					this.numGenerations = Integer.parseInt(value);
				}
				else if (property.contains("samplePercent"))
				{
					value = strTokens.nextToken(" ");
					this.samplePercent = value;
					try {
						float test = Float.parseFloat(samplePercent);
						if(test < 0.0 || test > 1.0) {
							System.out.print("Sample percent "+samplePercent+"is invalid.\n"
											 +"The value must be between 0.0 and 1.0");
							System.exit(1);
						}
					} catch(Exception ex) {
						System.out.print("Sample percent "+samplePercent+"is invalid.\n"
								 +"The value must be between 0.0 and 1.0");
						System.exit(1);
					}
				}
				else if(property.contains("algorithm"))
				{
					value = strTokens.nextToken(" ");
					if(value.equals(AlgorithmType.Genetic.getAlgName()))
						this.algorithm = AlgorithmType.Genetic;
					else
						this.algorithm = AlgorithmType.Bacteriological;
					
				}
				else if(property.contains("populationSize"))
				{
					value = strTokens.nextToken(" ");
					this.populationSize = Integer.parseInt(value);
				}
			}
		} while (!line.contains("}"));
	}

}
