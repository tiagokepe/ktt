package ufpr.inf.kepe.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import ufpr.inf.kepe.BacteriologicalAlgorithm;

public class ExecBash {

	public static Process run(String cmd, boolean background) throws IOException, InterruptedException
	{
		int result = 0;
		try {
			Process proc=Runtime.getRuntime().exec(cmd);
			if(!background)
				result = proc.waitFor();
			
			if(result==0)
			{
				System.out.println("Updating knobs!");
			}
/*			else
			{
				System.out.println("Finish with error!");
				System.out.println("Error:");
				
				BufferedWriter out = new BufferedWriter(new FileWriter(
	  						new File(BacteriologicalAlgorithm.getLogDir().getAbsolutePath()+"log.txt"), true));
				
				BufferedReader buffErrorStream = new BufferedReader(new InputStreamReader(proc.getErrorStream()));  
				String line = "";
				while( (line = buffErrorStream.readLine()) != null)
				{
					out.write(line+"\n");
				}
				out.close();
			}*/
			return proc;
		} catch (IOException e) {
			throw e; 
		} catch (InterruptedException e) {
			throw e; 
		}
	}
}
