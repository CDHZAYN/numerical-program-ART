package art;

import faultZone.FaultZone;
import faultZone.FaultZone_Point_Square;
import util.PartitionTree;
import util.DomainBoundary;
import util.Parameters;
import util.Testcase;
import util.Dimension;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SP-ART（2011）
 * 论文：Adaptive Random Testing By Static Partitioning
 * 大致方法：
 * 1、将输入空间随机分为某些区域，并根据如下规则将这些区域分为四种种类(以颜色区分)：
 *    (1)红色区域(red)：已经包含测试用例的区域；
 *    (2)黄色区域(yellow)：有两个或更多相邻红色区域且自身不包含测试用例的区域；
 *    (3)绿色区域(green)：有一个相邻红色区域且自身不包含测试用例的区域；
 *    (4)白色区域(white)：无相邻红色区域且自身不包含测试用例的区域。
 * 2、以白>绿>黄的优先级在选定区域中随机生成一个测试用例，
 * 3、不断循环过程2直到整个输入空间均为红色区域，此时将整个输入空间再重新置为白色并继续循环过程2直到找到故障为止。
 */
public class SP_ART extends AbstractART {

    public PartitionTree partitionTree;

    public SP_ART(DomainBoundary inputBoundary) {
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
            SP_ART sp_ART = new SP_ART(bd);
            //小run一下
            temp = sp_ART.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("第" + i + "次试验F_Measure：" + temp);
            sums += temp;
        }

        System.out.println("SP_ART当前参数：dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate);
        System.out.println("Fm: " + sums / (double) times + "  且最后的Fart/Frt: "
                + sums / (double) times * failrate);// 平均每次使用的测试用例数
    }

    @Override
    public Testcase bestCandidate() {
        candidate.clear();
        List<PartitionTree> partitionLeaves = partitionTree.getLeaveTreeNodes();    
        // 依次拿所有划分的范围来进行比较来判断两个划分是否相邻从而判定该分区的颜色
        for(PartitionTree goalTestcaseLeaf : partitionLeaves){
            int colorCnt = 0;
            for (PartitionTree partitionLeaf : partitionLeaves) {
            if(goalTestcaseLeaf != partitionLeaf){
                List<Dimension> goalDimensionList = goalTestcaseLeaf.getDomainBoundary().getList();
                List<Dimension> tempDimensionList = partitionLeaf.getDomainBoundary().getList();
                boolean isTouched = true;// 判断两个分区是否相邻
                for(int i = 0 ; i<goalDimensionList.size() ; i++){
                    Dimension goalDimension = goalDimensionList.get(i);
                    Dimension tempDimension = tempDimensionList.get(i);
                    if(!((goalDimension.getMax() == tempDimension.getMax()) || (goalDimension.getMax() == tempDimension.getMin()) || (goalDimension.getMin() == tempDimension.getMax()))){
                        isTouched = false;
                        break;
                    }
                }
                if(isTouched == true && partitionLeaf.getTestcaseNum() != 0){
                    colorCnt++;
                }
            }
        }
            if(goalTestcaseLeaf.getTestcaseNum() != 0){
                goalTestcaseLeaf.setcolor("red");
            }
            else{
                if(colorCnt == 0 ){
                    goalTestcaseLeaf.setcolor("white");
                }
                if(colorCnt == 1){
                    goalTestcaseLeaf.setcolor("green");
                }
                if(colorCnt > 1){
                    goalTestcaseLeaf.setcolor("yellow");
                }
            }      
}
        
        //根据优先级生成测试用例
        for (PartitionTree partitionLeaf : partitionLeaves){
            if(partitionLeaf.getcolor() == "white"){
                Testcase testcase = Testcase.generateCandates(1, partitionLeaf.getDomainBoundary().getList()).get(0);
                candidate.add(testcase);
                partitionTree.addTestcase(testcase);
                return testcase;
            }
            else if(partitionLeaf.getcolor() == "green"){
                Testcase testcase = Testcase.generateCandates(1, partitionLeaf.getDomainBoundary().getList()).get(0);
                candidate.add(testcase);
                partitionTree.addTestcase(testcase);
                return testcase;
            }
            else if(partitionLeaf.getcolor() == "yellow"){
                Testcase testcase = Testcase.generateCandates(1, partitionLeaf.getDomainBoundary().getList()).get(0);
                candidate.add(testcase);
                partitionTree.addTestcase(testcase);
                return testcase;
            }
        }

        // 若仅剩红色区域则将所有区域置为白色并重新运行算法
        for (PartitionTree partitionLeaf : partitionLeaves)
            partitionLeaf.setcolor("white");
        Testcase testcase = Testcase.generateCandates(1, partitionLeaves.get(new Random().nextInt(partitionLeaves.size())).getDomainBoundary().getList()).get(0);
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
