package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class SamplingRandom {

  public static class SelectorLine
       extends Mapper<Object, Text, Text, Text>{

    private int numberLines = 1;
    private Text result = new Text();

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {

      Configuration conf = context.getConfiguration();
      Float percentage = Float.parseFloat(conf.get("percentage.sample"));
      Random random = new Random();
      Float nextF = random.nextFloat(); 
      if(nextF <= percentage)
      {
          result.set(String.valueOf(numberLines));
          context.write(value, result);
          numberLines++;
          
    	  BufferedWriter out = new BufferedWriter(new FileWriter(
    			  						new File("/tmp/kepe/log/map_out.txt"), true));

    	  out.write("key = "+value.toString()+"\n");
    	  out.write("val = "+result.toString()+"\n");
    	  out.write("Percentagem = "+percentage+"\n");
    	  out.write("nextF = "+nextF+"\n");
    	  out.close();

      }
    }
  }

  public static class IntSumReducer
       extends Reducer<Text,Text,Text, Text> {
    private int valuesNumber = 1;
    private Text valueResult = new Text("");
    
    public Text mountKey(Text key, int valueNumber)
    {
        Text keyResult = new Text();
        keyResult.set(key.toString()+"|"+this.hashCode()+"|"+valueNumber);
    	return keyResult;
    }
    
    public void reduce(Text key, Iterable<Text> values,
                       Context context
                       ) throws IOException, InterruptedException {

      int sum = 0;
      for (Text val : values) {
    	  if(!val.toString().isEmpty()) 
    	  {
	          try {
	        	  BufferedWriter out = new BufferedWriter(new FileWriter(
	        	new File("/tmp/kepe/log/out.txt"), true));
	        	  
	        	  out.write("val = "+val.toString()+"\n");
	        	  out.write("key = "+key.toString()+"\n");
	        	  out.close();
	          } catch (IOException e) { }
	          //context.write(mountKey(key, valuesNumber), valueResult);
	          //context.write(new Text(this.hashCode()+"|"+valuesNumber), key);
	          //valuesNumber++;
	          context.write(key, valueResult);
      	  }
      }
      //result.set("1");
      //context.write(result, key);


//      Configuration conf = context.getConfiguration();
//      String percent = conf.get("test");

   }
  }

  public static void main(String[] args) throws Exception {
    System.out.println("########ARGS = "+args[0]+" ***"+args[1]);
    Configuration conf = new Configuration();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    if (otherArgs.length != 3) {
      System.err.println("Usage: SamplingRandom <percentage> <in> <out>");
      System.exit(2);
    }
    conf.set("percentage.sample", args[0]);

    Job job = new Job(conf, "Sampling");
    job.setJarByClass(SamplingRandom.class);
    job.setMapperClass(SelectorLine.class);
    //job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);

 //   job.setMapOutputKeyClass(Text.class);
 //   job.setMapOutputValueClass(IntWritable.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
    FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
