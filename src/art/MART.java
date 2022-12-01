package art;

import faultZone.FaultZone;
import faultZone.FaultZone_Point_Square;
import util.PartitionTree;
import util.Dimension;
import util.DomainBoundary;
import util.Parameters;
import util.Testcase;

import java.util.ArrayList;
import java.util.List;

/**
 * MART（2011）
 * 论文：Mirror adaptive random testing
 * 大致方法：
 * 1、按指定数目将输入空间平均划分，指定其中一个划分为主划分，其他划分为镜像划分。
 * 2、在主划分中随机生成一个测试用例，在每个镜像划分中以平移、镜像方法生成其他多个测试用例。
 */
public class MART extends AbstractART{

    public PartitionTree partitionTree;

    public PartitionTree mainPartition;

    public MART(DomainBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
        partitionTree = new PartitionTree(inputBoundary);
        partitionTree.partition(inputBoundary.dimensionOfInputDomain(), null);
        int index = (int)(Math.pow(2, inputBoundary.dimensionOfInputDomain()) * Math.random());
        while(index >= inputBoundary.dimensionOfInputDomain()) --index;
        mainPartition = partitionTree.getLeaveTreeNodes().get(index);
    }

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
            MART mART = new MART(bd);
            //小run一下
            temp = mART.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("第" + i + "次试验F_Measure：" + temp);
            sums += temp;
        }

        System.out.println("MART当前参数：dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate);
        System.out.println("Fm: " + sums / (double) times + "  且最后的Fart/Frt: "
                + sums / (double) times * failrate);// 平均每次使用的测试用例数
    }

    @Override
    public Testcase bestCandidate() {
        if(!candidate.isEmpty()){
            Testcase testcase = candidate.get(0);
            candidate.remove(0);
            return testcase;
        }
        //在主划分中随机生成一个测试用例
        Testcase testcase = Testcase.generateCandates(1, mainPartition.getDomainBoundary().getList()).get(0);
        partitionTree.addTestcase(testcase);

        //在其他划分中通过“镜像函数”生成其他候选测试用例
        //记录主划分中测试用例与每个维度的下限的差值，按照插值为其他划分生成测试用例。
        List<Double> delta = new ArrayList<>();
        for(int i = 0; i < inputBoundary.dimensionOfInputDomain(); ++i){
            Dimension dimension = mainPartition.getDomainBoundary().getList().get(i);
            double min = dimension.getMin();
            delta.add(testcase.getValue(i) - min);
        }
        List<PartitionTree> partitionLeaves = partitionTree.getLeaveTreeNodes();
        for(PartitionTree partitionLeaf : partitionLeaves){
            DomainBoundary domainBoundary = partitionLeaf.getDomainBoundary();
            List<Dimension> dimensions = domainBoundary.getList();

            //平移方法
            Testcase newTestcase1 = new Testcase();
            for(int i = 0; i < inputBoundary.dimensionOfInputDomain(); ++i){
                newTestcase1.list.add(dimensions.get(i).getMin() + delta.get(i));
            }
            candidate.add(newTestcase1);
            partitionTree.addTestcase(newTestcase1);

            //镜像方法
            Testcase newTestcase2 = new Testcase();
            for(int i = 0; i < inputBoundary.dimensionOfInputDomain(); ++i){
                newTestcase2.list.add(dimensions.get(i).getMax() - delta.get(i));
            }
            candidate.add(newTestcase2);
            partitionTree.addTestcase(newTestcase2);
        }

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
