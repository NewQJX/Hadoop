package prim;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PrimMain {
	/*
	 * selectedNodePath��Ҫ��ʼ��һ����ΪselectedNode.txt���ļ���
	 * ��������ѡ��Ľڵ㣬��ʼ���ļ��б���һ����������ʼ�ڵ㡣
	 * ���ѡ��ı߻���outputPath�б��棨���дһ������ÿ�ε���ѡ��ı�д��һ���ļ��У�
	 */
	public static void main(String[] args) throws Exception {
		if(args.length < 4) {
			System.err.println("usage: <selectedNodePath, inputPath, outputPath, nodeNumber>");
			return;
		}
		
		Configuration conf = new Configuration();
		conf.set("selectedNodePath", args[0]); // "/qjx/selectedNode/"
		String inputPath = args[1]; // "/qjx/input/"
		String outputPath = args[2]; // "/qjx/output/"
		int nodeNumber = Integer.parseInt(args[3]);
		FileSystem fs = FileSystem.get(conf);
		while (nodeNumber > 0) {
			conf.setInt("nodeNumber", nodeNumber);
			Job job = Job.getInstance(conf);

			job.setJarByClass(PrimMain.class);

			job.setNumReduceTasks(1); // set number of ReduceTask is 1

			job.setMapperClass(PrimMapper.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Edge.class);

			job.setReducerClass(PrimReducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Edge.class);

			FileInputFormat.setInputPaths(job, new Path(inputPath));
			FileOutputFormat.setOutputPath(job, new Path(outputPath + "/" + nodeNumber + "/"));

			job.waitForCompletion(true);
			nodeNumber--;

			FSDataInputStream in = null;
			FSDataOutputStream out = null;
			try {
				in = fs.open(new Path(outputPath + "/" + (nodeNumber + 1) + "/part-r-00000"));
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				out = fs.append(new Path(conf.get("selectedNodePath") + "/selectedNode.txt"));
				String line = null;
				while ((line = br.readLine()) != null) {
					String[] selectedNodes = line.split("\t");
					for (String selectedNode : selectedNodes) {
						out.writeBytes(selectedNode);
						out.writeBytes("\r");
					}
				}
			} finally {
				IOUtils.closeStream(in);
				IOUtils.closeStream(out);
			}

		}
	}

}
