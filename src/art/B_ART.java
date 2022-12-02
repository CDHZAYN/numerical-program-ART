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
 * B(Bisection)-ART（2004）
 * 论文：Adaptive Random Testing Through Dynamic Partitioning
 * 大致方法：
 * 1、然后平均划分输入空间，然后分别在每个分区中选择一个测试用例，
 * 2、再根据测试用例将每个分区一分为二（均分），然后在被均分后的分区中不包含测试用例的分区选择测试用例，
 * 3、不断循环过程2以确保测试用例分布广泛
 */
public class B_ART extends AbstractART {

    public PartitionTree partitionTree;

    public B_ART(DomainBoundary inputBoundary) {
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
            B_ART b_ART = new B_ART(bd);
            //小run一下
            temp = b_ART.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("第" + i + "次试验F_Measure：" + temp);
            sums += temp;
        }

        System.out.println("B_ART当前参数：dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate);
        System.out.println("Fm: " + sums / (double) times + "  且最后的Fart/Frt: "
                + sums / (double) times * failrate);// 平均每次使用的测试用例数
    }

    @Override
    public Testcase bestCandidate() {
        candidate.clear();
        List<PartitionTree> partitionLeaves = partitionTree.getLeaveTreeNodes();
        // 找到多个分区中不包含测试用例的分区
        PartitionTree noneTestcaseLeaf = partitionLeaves.get(0);
        for (PartitionTree partitionLeaf : partitionLeaves) {
            if (partitionLeaf.getTestcaseNum() == 0) {
                noneTestcaseLeaf = partitionLeaf;
                break;
            }
        }
        //若每个分区都包含有测试用例，则将目前的所有分区再一分为二，并随机选择一个新的叶子节点添加用例
        if (noneTestcaseLeaf.getTestcaseNum() != 0) {
            for (PartitionTree partitionLeaf : partitionLeaves)
                partitionLeaf.partition(2, null);
            noneTestcaseLeaf = noneTestcaseLeaf.getOneLeaf();
        }
        Testcase testcase = Testcase.generateCandates(1, noneTestcaseLeaf.getDomainBoundary().getList()).get(0);
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
