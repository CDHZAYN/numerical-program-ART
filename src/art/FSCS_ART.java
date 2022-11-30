package art;

import java.util.ArrayList;
import java.util.Random;

import faultZone.FaultZone;
import faultZone.FaultZone_Point_Square;
import util.*;

/**
 * FSCS 代码实现
 * 论文：2004-ASIAN-Adaptive random testing
 */
public class FSCS_ART extends AbstractART {

    int count = 1;

    //用输入来初始化该算法
    public FSCS_ART(DomainBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    //找到在输入空间中，距离其他输入最远的输入，将其作为下一个用于测试的输入返回
    @Override
    public Testcase bestCandidate() {
        //总之先生成10个新的候选输入
        this.Candidate.clear();
        this.Candidate = Testcase.generateCandates(10, inputBoundary.getList());

        Testcase p = null;
        double mindist, maxmin = 0;
        int index = -1;
        //如果之前没有进行过候选输入选择，则随机生成一个输入当作找到了
        if (total.size() == 0) {
            return Candidate.get(new Random().nextInt(Candidate.size()));
        }
        for (int i = 0; i < this.Candidate.size(); i++) {
            mindist = Double.MAX_VALUE;
            //对候选输入，挨个测试其与已使用过的输入的距离，记录该过程中的最小距离
            for (int j = 0; j < this.total.size(); j++) {
                double dist = Testcase.Distance(this.Candidate.get(i), this.total.get(j));
                if (dist < mindist) {
                    mindist = dist;
                }
            }
            //找到最小距离最大的一个候选输入，然后返回
            if (maxmin < mindist) {
                maxmin = mindist;
                index = i;
            }
        }

        return this.Candidate.get(index);
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

        System.out.println("FSCS_block当前参数：dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate); //输出当前参数信息
        System.out.println("Fm: " + sums / (double) times + "  且最后的Fart/Frt: "
                + sums / (double) times * failrate);// 平均每次使用的测试用例数

    }


    /**
     * 测试ART的效率，生成pointNum个测试用例所需的时间
     *
     * @param pointNum 需要生产的测试用例个数
     */
    @Override
    public void testEfficiency(int pointNum) { // 计算效率测试
        Testcase testcase = new Testcase(inputBoundary);
        while (total.size() < pointNum) { // 随机生成n个候选的测试用例
            total.add(testcase);
            Candidate = new ArrayList<Testcase>();
            for (int i = 0; i < 10; i++) {
                Candidate.add(new Testcase(inputBoundary));
            }
            testcase = bestCandidate();
        }
    }

}
