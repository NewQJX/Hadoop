package main;


import java.net.URI;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class KNNMain {

	public static void main(String[] args) throws Exception {
		
		Job job = Job.getInstance();		//����һ��mapreduce����
		job.setJobName("KNN");			//��������ΪKNN
		DistributedCache.addCacheFile(URI.create(args[2]), job.getConfiguration());		//�������л�ȡ���Լ����ļ�·��
		job.setJarByClass(KNNMain.class);				//���������������
		job.getConfiguration().setInt("k", Integer.parseInt(args[3]));		//�������л�ȡk��ֵ
		
		job.setMapperClass(KNNMapper.class);			//����map�׶ε�����
		job.setMapOutputKeyClass(Instance.class);		//����map�׶ε����key����
		job.setMapOutputValueClass(DistanceAndLabel.class);		//����map�׶����value����
		
		job.setCombinerClass(KNNCombiner.class);		//����combiner
		
		job.setReducerClass(KNNReducer.class);		//����reduce�׶ε�����
		job.setOutputKeyClass(Text.class);			//����reudce�׶����key����
		job.setOutputValueClass(NullWritable.class);	//����reduce�׶����value����
		
		FileInputFormat.addInputPath(job, new Path(args[0]));		//ѵ�����ļ���·��
		FileOutputFormat.setOutputPath(job, new Path(args[1]));		//����������·��
		
		job.waitForCompletion(true);		//��ʼִ������
	}

}
