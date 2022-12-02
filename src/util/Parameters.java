package util;

public interface Parameters {

	// 数值程序文件夹
	String realCodesPath = "src/realCodes";

	// faultZoneEntry输出文件夹
	String faultZoneResultDir = "src/../out/faultZone";

	// realCodeEntry输出文件夹
	String realCodeResultDir = "src/../out/realCode";

	//一次有效性或效率测试进行多少轮
	int testRoundNum = 1000;

	//当数值列表作为真实程序参数时（如List<byte>），列表中应有多少个元素
	int listParamNum = 10;

	//entry的效率测试要求得到多少个测试用例才停止
	int efficiencyTestcaseNum = 100;

	//entry的测试默认生成的测试用例维度
	int defaultDimensionNum = 3;

	//单次测试在强制停止前最多可以尝试生成测试用例多少次
	int maxAttemptNum = 1000;

	double defaultDimensionMin = -1000;

	double defaultDimensionMax = 1000;

	//输入距离计算的方法，不用管他，这个数值非常正常，使得距离的计算就是各个维度的距离的平方和再开平方
	double lp = 2.0;
}
