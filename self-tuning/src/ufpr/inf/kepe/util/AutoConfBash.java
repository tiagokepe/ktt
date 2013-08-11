package ufpr.inf.kepe.util;

import java.io.IOException;

public class AutoConfBash
{
	public static final String KEPE_AUTOCONF_HOME = System.getenv("KEPE_AUTOCONF_HOME"); 
	public static final String AUTOCONF_SERVER = KEPE_AUTOCONF_HOME + "/autoconf.conf";
	public static final String AUTOCONF_CTL = KEPE_AUTOCONF_HOME + "/bin/autoconfctl";
	
	private Process autoConfServer;
	
	public int runAutoConfServer() throws IOException, InterruptedException
	{
		//String cmd = AUTOCONF_HOME + "/" + AUTOCONF_SERVER;
		String cmd = AUTOCONF_SERVER;
		try {
			autoConfServer =  ExecBash.run(cmd, true);
		} catch (IOException e) {
			throw new IOException("Error running: "+cmd, e.getCause());
		} catch (InterruptedException e) {
			throw new InterruptedException("Error running: "+cmd);
		}
		
		if(autoConfServer != null)
			return 0;
		else
			return -1;
	}
	
	public void stopAutoConfServer()
	{
		if(autoConfServer != null)
			autoConfServer.destroy();
	}
	
	public static int execAutoConfCtl(String opts) throws IOException, InterruptedException
	{
		//String cmd = AUTOCONF_HOME + "/" + AUTOCONF_CTL + " " + opts;
		String cmd = AUTOCONF_CTL + " " + opts;
		Process proc;
		try {
			proc = ExecBash.run(cmd, false);
		} catch (IOException e) {
			throw new IOException("Error running: "+cmd, e.getCause());
		} catch (InterruptedException e) {
			throw new InterruptedException("Error running: "+cmd);
		}
		if(proc != null)
			return proc.exitValue();
		
		return 0;
	}
}