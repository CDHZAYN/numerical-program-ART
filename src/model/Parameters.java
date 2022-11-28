package model;

public interface Parameters {

	String baseFilePath = "D:/postGraduate/souceDatas/";

	//输入距离计算的方法，不用管他，这个数值非常正常，使得距离的计算就是各个维度的距离的平方和再开平方
	double lp = 2.0;

	int thread_pool_num = 4;

	int BucketMaxCount = 60;

	double R = 0.75;

	int k = 60;
	int m = 60;

	int p0 = 3;
}
