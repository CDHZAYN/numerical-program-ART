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
 * FP—ART（2020）
 * 论文：Adaptive random testing based on flexible partitioning
 * 大致方法：
 * 1、对于划分，采用RP-ART的思想，根据已经输入的测试用例为中心来将输入空间分成四份；
 * 2、然后选择所有划分中面积最大的划分作为下一个候选划分，
 * 3、但对于测试用例的选择采用IPT的思想，先在最大划分中生成若干(k)个用例，并命名为候选(candidate)用例
 *    然后挑选出其中离所在划分距离(距离各个维度边界的最小值)中最大的作为正式(formal)测试用例，即本次
 *    输入的测试用例；
 * 4、若找不到错误，则以本次输入的测试用例为中心循环重新回到过程1直到找到错误为止。
 */
public class FP_ART extends AbstractART {

    public PartitionTree partitionTree;

    public FP_ART(DomainBoundary inputBoundary) {
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
            FP_ART fp_ART = new FP_ART(bd);
            //小run一下
            temp = fp_ART.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("第" + i + "次试验F_Measure：" + temp);
            sums += temp;
        }

        System.out.println("FP_ART当前参数：dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate);
        System.out.println("Fm: " + sums / (double) times + "  且最后的Fart/Frt: "
                + sums / (double) times * failrate);// 平均每次使用的测试用例数
    }

    @Override
    public Testcase bestCandidate() {
        candidate.clear();

        //找到面积最大的叶节点(即最大的分区 此处为RP-ART的思想)
        List<PartitionTree> partitionLeaves = partitionTree.getLeaveTreeNodes();
        double maxSize = partitionLeaves.get(0).getSize();
        PartitionTree maxSizeLeaf = partitionLeaves.get(0);
        for (PartitionTree leaf : partitionLeaves) {
            if (maxSize < leaf.getSize()) {
                maxSize = leaf.getSize();
                maxSizeLeaf = leaf;
            }
        }
        //在找到的该节点范围内生成若干(k)个测试用例，并根据IPT的思想来获取理想测试用例。
        int k = 5; 
        candidate = Testcase.generateCandates(k, maxSizeLeaf.getDomainBoundary().getList());
        double maxDistance = 0;
        double minBoundaryDistance = 0;
        Testcase maxDistanceTestcase = null;
        for (Testcase testcase : candidate) {
            double dimensionLowBoundary = maxSizeLeaf.getDomainBoundary().getList().get(0).getMin();
            double dimensionUpBoundary = maxSizeLeaf.getDomainBoundary().getList().get(0).getMax();
            double upDistance = Math.abs(testcase.getValue(0) - dimensionUpBoundary);
            double lowDistance = Math.abs(testcase.getValue(0) - dimensionLowBoundary);
            if(upDistance<=lowDistance){
                minBoundaryDistance = upDistance;
            }
            else{
                minBoundaryDistance = lowDistance; 
            }
            for(int i = 1 ; i<maxSizeLeaf.getDomainBoundary().getList().size() ; i++){
                dimensionLowBoundary = maxSizeLeaf.getDomainBoundary().getList().get(i).getMin();
                dimensionUpBoundary = maxSizeLeaf.getDomainBoundary().getList().get(i).getMax();
                upDistance = Math.abs(testcase.getValue(i) - dimensionUpBoundary);
                lowDistance = Math.abs(testcase.getValue(i) - dimensionLowBoundary);
                if(upDistance<=lowDistance){
                    if(minBoundaryDistance>=upDistance){
                        minBoundaryDistance = upDistance;
                    }
                }
                else{
                    if(minBoundaryDistance>=lowDistance){
                        minBoundaryDistance = lowDistance;
                    }
                }
            }

            if (minBoundaryDistance > maxDistance) {
                maxDistance = minBoundaryDistance;
                maxDistanceTestcase = testcase;
            }
        }
        partitionTree.addTestcase(maxDistanceTestcase);

        Testcase divide = new Testcase();
        for (int i = 0; i < inputBoundary.dimensionOfInputDomain(); ++i) {
            divide.list.add(((double) ( maxDistanceTestcase.getValue(i))) * Math.random());
        }
        maxSizeLeaf.partition(2, divide);
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
