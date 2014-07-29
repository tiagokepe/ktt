package br.ufpr.inf.lbd;

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
    return index.getTunedKnobs(k);
  }

  public boolean add(Knobs k) throws RemoteException {
    return index.add(k);
  }

  public boolean remove(Knobs k) throws RemoteException {
    return index.remove(k);
  }

  public boolean update(Knobs k) throws RemoteException {
    return index.update(k);
  }

  public void list() throws RemoteException {
    index.list();
  }

  public void show(Knobs k) throws RemoteException {
    index.show(k);
  }

  public static void main(String args[]) {
    if (System.getSecurityManager() == null)
      System.setSecurityManager(new RMISecurityManager());

    try {
      AutoConfImpl server = new AutoConfImpl();
      AutoConf stub = (AutoConf) UnicastRemoteObject.exportObject(server, 0);
      Registry registry = LocateRegistry.createRegistry(50123);
      registry.rebind("//localhost/AutoConf", stub);
      System.out.println("AutoConf: AutoConf server is running...");
    } catch (Exception e) {
      System.err.println("AutoConf error: " + e.getMessage());
      e. printStackTrace();
    }
  }
}
