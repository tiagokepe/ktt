package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class RandomSampling
{

	public static class SortLines extends Mapper<LongWritable, Text, LongWritable, Text>
	{

	    private int numberLines = 1;
	
	    public void map(LongWritable key, Text value, Context context
	                    ) throws IOException, InterruptedException
	    {
	    	context.write(key, value);
	    }
	}

	public static class ReducerSampling extends Reducer<LongWritable,Text,Text,Text>
	{
	    public void reduce(LongWritable key, Iterable<Text> values, Context context
	                       ) throws IOException, InterruptedException
	    {
			Configuration conf = context.getConfiguration();
			Float percentage = Float.parseFloat(conf.get("percentage.sampling"));

			for(Text value: values)
			{
				Random random = new Random();
				Float nextF = random.nextFloat();
				
				if(nextF <= percentage)
				{
				    context.write(value, new Text(""));
				    
/*				  BufferedWriter out = new BufferedWriter(new FileWriter(
						  						new File("/tmp/kepe/log/map_out.txt"), true));
				
				  out.write("key = "+key.toString()+"\n");
				  out.write("val = "+value.toString()+"\n");
				  out.write("Percentagem = "+percentage+"\n");
				  out.write("nextF = "+nextF+"\n");
				  out.close();
*/				  
				}
			}
	    }
	}

	public static void main(String[] args) throws Exception
	{
		System.out.println("########ARGS = "+args[0]+" ***"+args[1]);
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 3) {
		  System.err.println("Usage: SamplingRandom <percentage> <in> <out>");
		  System.exit(2);
		}
		conf.set("percentage.sampling", args[0]);
		
		Job job = new Job(conf, "sampling");
		job.setJarByClass(RandomSampling.class);
		job.setMapperClass(SortLines.class);
		job.setReducerClass(ReducerSampling.class);
		
		//job.setOutputKeyClass(Text.class);
		//job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
