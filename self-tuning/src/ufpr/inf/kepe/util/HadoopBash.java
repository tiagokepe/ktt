package ufpr.inf.kepe.util;

import java.io.IOException;

public class HadoopBash {
	public static final String KEPE_HADOOP_HOME = System.getenv("KEPE_HADOOP_HOME");
	public static final String HADOOP = KEPE_HADOOP_HOME + "/bin/hadoop"; 
	
	public static int execHadoop(String opts) throws IOException, InterruptedException
	{
		String cmd = HADOOP + " " + opts;
		Process proc = null;
		try {
			proc = ExecBash.run(cmd, false);
		} catch (IOException e) {
			throw new IOException("Error running: "+cmd);
		} catch (InterruptedException e) {
			throw new InterruptedException("Error running: "+cmd);
		}
		if(proc != null)
			return proc.exitValue();
		
		return 0;
	}
}