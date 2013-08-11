package br.ufpr.inf.lbd;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class AutoConfIndex {
  static TreeMap<String, Knobs> tree = new TreeMap<String, Knobs>();

  /* TODO: Optimize Job Configuration */
  public Knobs optimize(Knobs k) {
    return k;
  }

  /* TODO: This add should lock the tree to add new knobs
   *       Translates Knobs to Configuration */
  public Knobs getTunedKnobs(Knobs k) {
    Knobs a;
    String name = k.getJobName().split(" ")[0];

    if (!k.getJobName().matches("no-name")) {
      System.out.println(" *** AutoConf: Configuring " + name);
    }

    if (tree.containsKey(name)) {
      a = tree.get(name);
      a.setJobName(k.getJobName());
      return a;
    } else {
      // Uncomment the lines below in case
      // you want jobs writing the Knobs.resources
      //optimize(k);
      //tree.put(name, k);
      return k;
    }
  }

  public boolean add(Knobs k) {
    System.out.println(" *** AutoConf: Adding " + k.getJobName());
    if (tree.containsKey(k.getJobName())) {
      tree.put(k.getJobName(), k);
      return true;
    } else {
      tree.put(k.getJobName(), k);
      return false;
    }
  }

  public boolean update(Knobs k) {
    System.out.println(" *** AutoConf: Updating " + k.getJobName());
    if (tree.containsKey(k.getJobName())) {
      tree.put(k.getJobName(), k);
      return true;
    } else {
      tree.put(k.getJobName(), k);
      return false;
    }
  }

  public boolean remove(Knobs k) {
    if (tree.containsKey(k.getJobName())) {
      System.out.println(" *** AutoConf: Removing " + k.getJobName());
      tree.remove(k.getJobName());
      return true;
    }
    return false;
  }

  public void list() {
    if (tree.isEmpty()) {
      System.out.println("AutoConf: List: There is no tuned jobs.");
    } else {
      Set set = tree.keySet();
      System.out.println("AutoConf: List: The following jobs are indexed.");
      for (Iterator i = set.iterator(); i.hasNext();){
        String jobName = (String) i.next();
        System.out.println(" +++ " + jobName + " has " + tree.get(jobName).getResources().size() + " tuned knobs.");
      }
    }
  }

  public void show(Knobs k) {
    System.out.println(k.getJobName());
    if (tree.containsKey(k.getJobName())) {
      Knobs knobs = tree.get(k.getJobName());
      knobs.showConfiguration();
    }
  }
}
