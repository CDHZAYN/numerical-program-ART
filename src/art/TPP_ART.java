package art;

import faultZone.FaultZone;
import faultZone.FaultZone_Point_Square;
import faultZone.PartitionTree;
import util.Dimension;
import util.DomainBoundary;
import util.Parameters;
import util.Testcase;

import java.util.ArrayList;
import java.util.List;

/**
 * TPP—ART（2011）
 * 论文：Adaptive Random Testing Based on Two-Point Partitioning
 * 大致方法：
 * 1、在当前最大的划分CurReg中，生成多个候选输入；
 * 2、如果CurReg中没有已测试输入，则直接进行测试；
 * 3、如果CurReg中已有测试输入T1，找到与T1距离最远的候选输入T2；
 * 4、若找不到错误，则CurReg以T1与T2的中点为参照点划分，找到新的最大划分，从1开始。
 */
public class TPP_ART extends AbstractART {

    public PartitionTree partitionTree;

    public TPP_ART(DomainBoundary inputBoundary) {
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
            FSCS_ART fscs_block = new FSCS_ART(bd);
            //小run一下
            temp = fscs_block.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("第" + i + "次试验F_Measure：" + temp);
            sums += temp;
        }

        System.out.println("FSCS_block当前参数：dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate);
        System.out.println("Fm: " + sums / (double) times + "  且最后的Fart/Frt: "
                + sums / (double) times * failrate);// 平均每次使用的测试用例数
    }

    @Override
    public Testcase bestCandidate() {
        candidate.clear();

        //找到面积最大的叶节点
        List<PartitionTree> partitionLeaves = partitionTree.getLeaveTreeNodes();
        double maxSize = partitionLeaves.get(0).getSize();
        PartitionTree maxSizeLeaf = partitionLeaves.get(0);
        for (PartitionTree leaf : partitionLeaves) {
            if (maxSize < leaf.getSize()) {
                maxSize = leaf.getSize();
                maxSizeLeaf = leaf;
            }
        }
        //在找到的该节点范围内生成10个测试用例，找到与已测试距离最大的测试用例。
        candidate = Testcase.generateCandates(10, maxSizeLeaf.getDomainBoundary().getList());
        if(maxSizeLeaf.gettestcaseList().isEmpty()){
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

        //找到T1、T2的中点，形成4个子划分。
        Testcase midPoint = new Testcase();
        for (int i = 0; i < inputBoundary.dimensionOfInputDomain(); ++i)
            midPoint.list.add(((double) (testedTestcase.getValue(i) + maxDistanceTestcase.getValue(i))) / 2.0);
        maxSizeLeaf.partition(2, midPoint);

        return maxDistanceTestcase;
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
