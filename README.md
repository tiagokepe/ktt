ktt
===

This project aims to find better Hadoop configuration knobs for MR jobs.

The engine generates samples based on the data set stored on the cluster for each MR job.

Afterward, it runs the job on these samples using the bacteriological algorithm to find better configuration knobs.

The AutoConf (RMI methods) module intercepts the Hadoop execution and sets up the new knobs.
