package art;

import java.util.*;

import faultZone.FaultZone;
import faultZone.FaultZone_Point_Square;
import util.*;

/**
 * RRT（2004）
 * 论文：A revisit of adaptive random testing by restriction,
 * 大致方法：
 * 1. 随机选择一个用例进行测试
 * 2. 设置一个限制倍数r
 * 3. 利用r计算以之前测试的用例为中心的排除半径
 * 4. 将半径内的所有用例排除，生成新的测试用例域，从中随机选择下一个用例
 */

public class RRT extends AbstractART{

    double rate = 0.8;

    //用输入来初始化该算法
    public RRT(DomainBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
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
            RRT rrt = new RRT(bd);
            //小run一下
            temp = rrt.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("第" + i + "次试验F_Measure：" + temp);
            sums += temp;
        }

        System.out.println("RRT当前参数：dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate);
        System.out.println("Fm: " + sums / (double) times + "  且最后的Fart/Frt: "
                + sums / (double) times * failrate);// 平均每次使用的测试用例数
    }

    public Double calR(double rate, Testcase tc){
        ArrayList<Double>Tc=tc.list;
        Double size = Collections.max(Tc) - Collections.min(Tc);
        int len = Tc.size();
        Double r = Math.pow(rate*size,1.0/len);
        return r;
    }

    @Override
    public Testcase bestCandidate() {
        this.candidate.clear();
        Testcase tc = new Testcase(inputBoundary);
        DomainBoundary newBoundary = inputBoundary;
        double r = calR(rate , tc);
        int count = 0;
        while(count < inputBoundary.getList().size() && ((inputBoundary.getList().get(count).getMin() + r)> tc.getValue(count) || (inputBoundary.getList().get(count).getMax() - r) < tc.getValue(count))) {
            count++;
            tc = new Testcase(inputBoundary);
        }
        return tc;
    }

    @Override
    public void testEfficiency(int pointNum) { // 计算效率测试
        Testcase testcase = new Testcase(inputBoundary);
        while (total.size() < pointNum) { // 随机生成n个候选的测试用例
            total.add(testcase);
            candidate = new ArrayList<Testcase>();
            for (int i = 0; i < 10; i++) {
                candidate.add(new Testcase(inputBoundary));
            }
            testcase = bestCandidate();
        }
    }
}