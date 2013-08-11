package org.apache.hadoop.conf;

import org.apache.hadoop.mapred.JobConf;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class AutoConfImpl implements AutoConf {
  AutoConfIndex index;

  public AutoConfImpl() throws RemoteException {
    this.index = new AutoConfIndex();
  }

  public Knobs autoConfigure(Knobs k) throws RemoteException {
    return index.configure(k);
  }

  public static void main(String args[]) {
    /* Create and install a security manager */
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    try {
      AutoConfImpl server = new AutoConfImpl();
      AutoConf stub = (AutoConf) UnicastRemoteObject.exportObject(server, 0);
      Registry registry = LocateRegistry.createRegistry(50123);
      registry.rebind("//localhost/AutoConf",stub);
      System.out.println("AutoConf: Waiting for jobs...");
    } catch (Exception e) {
      System.err.println("AutoConf error: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
