package art;

import java.util.*;

import faultZone.FaultZone;
import faultZone.FaultZone_Point_Square;
import util.*;

/**
 * KD-RRT（2021）
 * 论文：KD-RRT: Restricted Random Testing based on K-Dimensional Tree
 * 大致方法：
 * 1. 随机选择一个用例进行测试
 * 2. 设置一个限制倍数R
 * 3. 利用R和输入域计算以之前测试的用例为中心的排除半径
 * 4. 将半径内的所有用例排除，生成新的测试用例域，从中随机选择下一个用例
 */

public class KD_RRT extends AbstractART{

    double R = 0.75;
    double PI = 3.14;

    //用输入来初始化该算法
    public KD_RRT(DomainBoundary inputBoundary) {
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
            KD_RRT kd_rrt = new KD_RRT(bd);
            //小run一下
            temp = kd_rrt.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("第" + i + "次试验F_Measure：" + temp);
            sums += temp;
        }

        System.out.println("KD_RRT当前参数：dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate);
        System.out.println("Fm: " + sums / (double) times + "  且最后的Fart/Frt: "
                + sums / (double) times * failrate);// 平均每次使用的测试用例数
    }

    public Double update_r(double rate, Testcase tc){
        ArrayList<Double>Tc=tc.list;
        Double size = Collections.max(Tc) - Collections.min(Tc);
        int len = Tc.size();
        Double r = Math.pow(R*size/PI,0.5);
        return r;
    }

    @Override
    public Testcase bestCandidate() {
        this.candidate.clear();
        Testcase tc = new Testcase(inputBoundary);
        double r = update_r(R , tc);
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