package art;

import java.util.*;

import faultZone.FaultZone;
import faultZone.FaultZone_Point_Square;
import util.*;

/**
 * RP-ART（2004）
 * 论文：Adaptive Random Testing Through Dynamic Partitioning
 * 大致方法：
 * 1. 首先测试随机生成的用例1，并生成候选用例组
 * 2. 找到距离1最远的距离2，并通过随机数将候选用例组进行随机划分
 */

public class RP_ART extends AbstractART{

    //用输入来初始化该算法
    public RP_ART(DomainBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
        partitionTree = new PartitionTree(inputBoundary);
    }
    public PartitionTree partitionTree;

    public static void main(String args[]){
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
            RP_ART RP_art = new RP_ART(bd);
            //小run一下
            temp = RP_art.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("第" + i + "次试验F_Measure：" + temp);
            sums += temp;
        }

        System.out.println("RP_ART当前参数：dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate);
        System.out.println("Fm: " + sums / (double) times + "  且最后的Fart/Frt: "
                + sums / (double) times * failrate);// 平均每次使用的测试用例数
    }

    @Override
    public Testcase bestCandidate() {
        candidate.clear();
        List<PartitionTree> partitionLeaves = partitionTree.getLeaveTreeNodes();
        double maxSize = partitionLeaves.get(0).getSize();
        PartitionTree maxSizeLeaf = partitionLeaves.get(0);
        for (PartitionTree leaf : partitionLeaves) {
            if (maxSize < leaf.getSize()) {
                maxSize = leaf.getSize();
                maxSizeLeaf = leaf;
            }
        }
        //生成10个测试用例，找到与已测试距离最大的测试用例。
        candidate = Testcase.generateCandates(10, maxSizeLeaf.getDomainBoundary().getList());
        if (maxSizeLeaf.gettestcaseList().isEmpty()) {
            partitionTree.addTestcase(candidate.get(0));
            return candidate.get(0);
        }
        Testcase testedTestcase = maxSizeLeaf.gettestcaseList().get(0);
        double maxDistance = 0;
        Testcase maxDistanceTestcase = null;
        for (Testcase testcase : candidate) {
            double distance = Testcase.Distance(testcase, testedTestcase);
            if (distance > maxDistance) {
                maxDistance = distance;
                maxDistanceTestcase = testcase;
            }
        }
        partitionTree.addTestcase(maxDistanceTestcase);

        //进行随机划分
        Testcase divide = new Testcase();
        Random r = new Random();
        int r1 = r.nextInt(5);
        for (int i = 0; i < inputBoundary.dimensionOfInputDomain(); ++i) {
            divide.list.add(((double) ( maxDistanceTestcase.getValue(i))) * Math.random());
        }
        maxSizeLeaf.partition(2, divide);
        return maxDistanceTestcase;
    }

    @Override
    public void testEfficiency(int pointNum) {
        Testcase testcase = new Testcase(inputBoundary);
        while (total.size() < pointNum) {
            total.add(testcase);
            testcase = bestCandidate();
        }
    }
}