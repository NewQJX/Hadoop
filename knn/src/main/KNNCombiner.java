package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;
import org.apache.hadoop.mapreduce.Reducer;
import org.mockito.internal.util.ArrayUtils;

import com.google.inject.spi.Message;
import com.sun.jersey.api.MessageException;

import util.Sort;


/*
 * combiner�׶λ�ȡ����map�׶ε����������ͬ���Լ�������������ÿ��ѵ���������������𣬺ϲ���һ����Ϊcombiner������
 * ���Ϊ��(���Լ����������ؽڵ������������k������)
 */
public class KNNCombiner extends Reducer<Instance, DistanceAndLabel, Instance, DistanceAndLabel> {
	
	private int k;		//��������k

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		this.k = context.getConfiguration().getInt("k", 1);			//��ȡk��ֵ
	}
	
	@Override
	protected void reduce(Instance testInstance, Iterable<DistanceAndLabel> iter,Context context)
			throws IOException, InterruptedException {
		
		ArrayList<DistanceAndLabel> list = new ArrayList<>();		//��ʼ��һ�����ϣ����������valueֵ
		
		for(DistanceAndLabel d : iter) {			//��iter�е�Ԫ�أ����뵽list������
			DistanceAndLabel tmp = new DistanceAndLabel(d.distance, d.label);
			list.add(tmp);		
		}
		
		list = Sort.getNearest(list, k);			//�ڸò���������ѵ�������ľ����У��ҵ������k�����룬�Ͷ�Ӧ������ǩ
		for(DistanceAndLabel dal : list) {		//���	(���Լ����������ؽڵ������������k������)
			context.write(testInstance, dal);
		}

	}

	

	
	
	

}
