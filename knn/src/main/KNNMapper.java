package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import com.sun.jersey.api.MessageException;

import util.Distance;

/*
 * map�׶ε�����Ϊѵ�����ı��ļ���ʽΪ��	1.0 2.0 3.0 1		���һ��Ϊ����ǩ
							1.0 2.1 3.1 1
							
	���Ϊ��(���������� DistanceAndLabel)		DistanceAndLabel�����˾�������
 */
public class KNNMapper extends Mapper<LongWritable, Text, Instance, DistanceAndLabel> {

	private ArrayList<Instance> testSet = new ArrayList<Instance>(); // ���Լ�
	// k1Ϊѵ��������ƫ���� v1Ϊƫ������Ӧ�����ݣ����ԣ����

	@Override
	protected void map(LongWritable k1, Text v1, Context context) throws IOException, InterruptedException {
		Instance trainInstance = new Instance(v1.toString()); // һ��ѵ�����е�����
		double label = trainInstance.getLable(); // ��ȡѵ���������

		// �������Լ������㵽ѵ�������ľ���
		for (Instance testInstance : testSet) {
			// �������
			double dis = 0.0;
			try {
				dis = Distance.EuclideanDistance(testInstance.getAtrributeValue(), trainInstance.getAtrributeValue());	//������Լ�������ѵ����������ŷʽ����
				DistanceAndLabel dal = new DistanceAndLabel(dis, label);	//��������ľ����ѵ����������Ӧ������ǩ��װ��DistanceAndLabel����
				context.write(testInstance, dal);	//map���
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// ��ÿ��map�ڵ���ز��Լ�   ��ʽ��	3.3 6.9 8.8 -1				���һ��Ϊ����ǩ������ʼ��Ϊ-1
//								2.5 3.3 10.0 -1
	//�����Լ��е�ÿһ�ж���װ��һ��Instance���󣬼��뵽testSet������
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		Path[] testFile = DistributedCache.getLocalCacheFiles(context.getConfiguration());	//��ȡ����Ĳ��Լ��ļ�·��
		BufferedReader br = null;		//����һ���ַ�������
		String line;
		for (int i = 0; i < testFile.length; i++) {
			br = new BufferedReader(new FileReader(testFile[0].toString()));		//���ݲ��Լ��ļ�·����ʼ���ַ�������
			while ((line = br.readLine()) != null) {		//���ж�ȡ���Լ��ļ�
				Instance testInstance = new Instance(line);		//���ݲ��Լ���һ�У�����һ��Instance����
				testSet.add(testInstance);				//���뵽testSet������
			}
		}

	}

}
