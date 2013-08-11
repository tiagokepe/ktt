package org.apache.hadoop.conf;

import org.apache.hadoop.mapred.JobConf;

import java.util.TreeMap;

public class AutoConfIndex {
  static TreeMap<String, Knobs> tree = new TreeMap<String, Knobs>();

//  public class jobComparator implements Comparator<String> {
//    public int compare(String job1, String job2) {
//      int result = job1.compareToIgnoreCase(job2);
//      if (result == 0)
//        result = job1.compareTo(job2);
//      return result;
//    }
//  }

  /* TODO: Optimize Job Configuration */
  public Knobs optimize(Knobs k) {
    k.setJobName(k.getJobName() + "_AutoConfigured");
    return k;
  }

  /* TODO: This add should lock the tree to add new knobs
   *       Translates Knobs to Configuration */
  public Knobs configure(Knobs k) {
    Knobs tunedKnobs = null;
    if (tree.containsKey(k.getJobName())) {
      tunedKnobs = tree.get(k.getJobName());
    } else {
      tunedKnobs = optimize(k);
      tree.put(tunedKnobs.getJobName(), tunedKnobs);
    }
    return tunedKnobs;
  }
}
