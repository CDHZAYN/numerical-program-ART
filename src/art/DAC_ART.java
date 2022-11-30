package art;

import faultZone.FaultZone;
import faultZone.FaultZone_Point_Square;
import util.PartitionTree;
import util.DomainBoundary;
import util.Parameters;
import util.Testcase;

import java.util.ArrayList;
import java.util.List;

/**
 * DAC—ART（2013 13th International Conference on Quality Software）
 * 论文：The ART of Divide and Conquer
 * 大致方法：
 * 1、平均划分输入空间，
 * 2、找到其中已测试输入最少的划分进行测试，
 * 3、不断循环2直到每个划分中的已测试输入均达到指定数量。
 * 4、再次平均划分每个划分。
 */
public class DAC_ART extends AbstractART {

    int maxTestcasePerNode = 1;

    public PartitionTree partitionTree;

    public DAC_ART(DomainBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
        partitionTree = new PartitionTree(inputBoundary);
    }

    public static void main(String args[]) {
        int times = 3000;//一次执行，该算法就重复3000次
        long sums = 0;// 初始化使用的测试用例数
        int temp = 0;// 初始化测试用例落在失效域的使用的测试用例的个数

        ArrayList<Integer> result = new ArrayList<>();

        //统一p=2，表示计算输入间距离按正常方法计算（各维度距离平方和开平方）
        double p = Parameters.lp;
        double failrate = 0.005;
        int dimension = 2;
        //二维输入（即一次输入两个参数），两个数值参数的上限（5000）、下限（5000）相同
        DomainBoundary bd = new DomainBoundary(dimension, -5000, 5000);

        for (int i = 1; i <= times; i++) {
            //指定使用这种fault zone
            FaultZone fz = new FaultZone_Point_Square(bd, failrate);
            DAC_ART dac_ART = new DAC_ART(bd);
            //小run一下
            temp = dac_ART.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("第" + i + "次试验F_Measure：" + temp);
            sums += temp;
        }

        System.out.println("DAC_ART当前参数：dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate);
        System.out.println("Fm: " + sums / (double) times + "  且最后的Fart/Frt: "
                + sums / (double) times * failrate);// 平均每次使用的测试用例数
    }

    @Override
    public Testcase bestCandidate() {
        candidate.clear();
        List<PartitionTree> partitionLeaves = partitionTree.getLeaveTreeNodes();
        // 找到包含已测试用例最少的划分
        PartitionTree minTestcaseLeaf = partitionLeaves.get(0);
        for (PartitionTree partitionLeaf : partitionLeaves) {
            if (minTestcaseLeaf.getTestcaseNum() > partitionLeaf.getTestcaseNum()
                    && partitionLeaf.getTestcaseNum() < maxTestcasePerNode) {
                minTestcaseLeaf = partitionLeaf;
            }
        }
        //若找不到，则进行一次集体划分，随机选择一个新的叶子节点添加用例
        if (minTestcaseLeaf.getTestcaseNum() >= maxTestcasePerNode) {
            for (PartitionTree partitionLeaf : partitionLeaves)
                partitionLeaf.partition(2, null);
            minTestcaseLeaf = minTestcaseLeaf.getOneLeaf();
        }
        Testcase testcase = Testcase.generateCandates(1, minTestcaseLeaf.getDomainBoundary().getList()).get(0);
        candidate.add(testcase);
        partitionTree.addTestcase(testcase);
        return testcase;
    }

    @Override
    public void testEfficiency(int pointNum) {
        Testcase testcase = new Testcase(inputBoundary);
        while (total.size() < pointNum) { // 随机生成n个候选的测试用例
            total.add(testcase);
            testcase = bestCandidate();
        }
    }
}
