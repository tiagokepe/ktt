package org.apache.hadoop.conf;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AutoConf extends Remote {
  Knobs autoConfigure(Knobs conf) throws RemoteException;
}
