package br.ufpr.inf.lbd;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AutoConf extends Remote {
  Knobs autoConfigure(Knobs knobs) throws RemoteException;
  boolean add(Knobs knobs) throws RemoteException;
  boolean remove(Knobs knobs) throws RemoteException;
  boolean update(Knobs knobs) throws RemoteException;
  void list() throws RemoteException;
  void show(Knobs knobs) throws  RemoteException;
}
