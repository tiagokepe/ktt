/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.conf;

import org.apache.hadoop.mapred.JobConf;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Iterator;

/** Base class for things that may be configured with a {@link Configuration}. */
public class Configured implements Configurable {

  private Configuration conf = null;

  /** Construct a Configured. */
  public Configured() {
    this(null);
  }

  /** Construct a Configured. */
  public Configured(JobConf conf) {
    setConf(conf);
  }

//  inherit javadoc
//  public void setConf(Configuration conf) {
//    this.conf = conf;
//  }

  /* TODO: add support to AutoConf */
  public void setConf(Configuration conf) {
    this.conf = conf;
//    if (conf == null) {
//      this.conf = null;
//    } else {
//      try {
//        Registry registry = LocateRegistry.getRegistry(50123);
//        AutoConf autoConf = (AutoConf) registry.lookup("//localhost/AutoConf");
//        Knobs originalResources = new Knobs(conf);
//        Knobs tunedResources = autoConf.autoConfigure(originalResources);
//        tunedResources.copyResourcesTo(conf);
//        this.conf = conf;
//      }
//      catch (Exception e) {
//        System.err.println(e.getMessage());
//        e.printStackTrace();
//      }
//    }
  }

  // inherit javadoc
  public Configuration getConf() {
    return conf;
  }
}
