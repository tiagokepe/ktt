package ufpr.inf.kepe.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Properties
{
	private String jarPath;
	private String jobClassName;
	private String pathInputDirHDFS;
	private String pathOutputDirHDFS;
	private int numGenerations;
	
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
			}
		} while (!line.contains("}"));
	}

}
