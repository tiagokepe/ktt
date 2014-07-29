package org.apache.hadoop.conf;

import java.util.Iterator;
import java.util.Map;
import java.io.Serializable;

public class Knobs implements Serializable {
  String jobName = null;
  String [][] resources = null;

  public Knobs(Configuration conf) {
    setJobName("no-name");
    setResources(conf);
  }

  public Knobs(String name, Configuration conf){
    setJobName(name);
    setResources(conf);
  }

//  private void setResources(Configuration conf) {
//    System.out.println("AutoConf: Adding the following resources to the tuning index.");
//    this.resources = new Map.Entry[conf.size()];
//    Iterator i = conf.iterator();
//    for (Integer counter = 0; i.hasNext(); counter++) {
//      this.resources[counter] = (Map.Entry<String, String>) i.next();
//      System.out.println(this.resources[counter].getKey() + " : " + this.resources[counter].getValue());
//    }
//    System.out.println("");
//  }

  private void setResources(Configuration conf) {
    System.out.println("AutoConf: Adding the following resources to the tuning index.");
    Map.Entry<String, String> r[] = new Map.Entry[conf.size()];
    this.resources = new String[conf.size()][2];

    Iterator i = conf.iterator();
    for (Integer counter = 0; i.hasNext(); counter++) {
      r[counter] = (Map.Entry<String, String>) i.next();
      this.resources[counter][0] = r[counter].getKey();
      this.resources[counter][1] = r[counter].getValue();
      System.out.println(r[counter].getKey() + " : " + r[counter].getValue());
    }
    r = null;
  }


  public String [][] getResources(){
    return this.resources;
  }

  public void setJobName(String name) {
    if (name == null)
      this.jobName = "no-name";
    this.jobName = name;
  }

  public String getJobName(){
    return this.jobName;
  }

  /** Overwrite the configuration of conf */
  public void copyResourcesTo(Configuration conf){
    Integer i = 0;
    for (i=0; i < resources.length; i++)
      conf.set(resources[i][0], resources[i][1]);
  }
}
