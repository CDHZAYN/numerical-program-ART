package art;

import faultZone.FaultZone;
import faultZone.FaultZone_Point_Square;
import util.DomainBoundary;
import util.Parameters;
import util.Testcase;

import java.util.ArrayList;
import java.util.Random;

//TODO: 算法介绍
/**
 * ECP_FSCS_ART（2007）
 * Enhancing Adaptive Random Testing through Partitioning by Edge and Centre
 * 大致方法：
 * 1.将输入域划分为等面积，然后对未取过testcase的输入域取testcase
 * 2.对candidate，找到最小距离最大的testcase，并返回
 */
public class ECP_FSCS_ART extends AbstractART{

    private DomainBoundary inputBoundary;

    public ECP_FSCS_ART(DomainBoundary inputBoundary){
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
            ECP_FSCS_ART ecp_fscs_art = new ECP_FSCS_ART(bd);
            //小run一下
            temp = ecp_fscs_art.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("第" + i + "次试验F_Measure：" + temp);
            sums += temp;
        }

        System.out.println("ECP_FSCS_ART当前参数：dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate);
        System.out.println("Fm: " + sums / (double) times + "  且最后的Fart/Frt: "
                + sums / (double) times * failrate);// 平均每次使用的测试用例数
    }

    @Override
    public Testcase bestCandidate() {
        //TODO:找到下一个测试用例的方法
        this.candidate.clear();
        int partition_times = 10;
        Testcase tc = new Testcase(inputBoundary);  //随机生成一个测试用例
        int[] hit = new int[10];
        for(int i:hit){
            i = 0;
        }
        while (partition_times>0){                  //模拟对输入域的划分
            double rand = Math.random()*10;
            int num = (int)Math.floor(rand);
            if(hit[num] == 0){
                hit[num] ++ ;
                candidate.add(tc);
                partition_times --;
            }else{
                continue;
            }
            tc = new Testcase(inputBoundary);
        }
        double mindist, maxmin = 0;
        int index = -1;
        //如果之前没有进行过候选输入选择，则随机生成一个输入当作找到了
        if (total.size() == 0) {
            return candidate.get(new Random().nextInt(candidate.size()));
        }
        for (int i = 0; i < this.candidate.size(); i++) {
            mindist = Double.MAX_VALUE;
            //对候选输入，挨个测试其与已使用过的输入的距离，记录该过程中的最小距离
            for (int j = 0; j < this.total.size(); j++) {
                double dist = Testcase.Distance(this.candidate.get(i), this.total.get(j));
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
        return this.candidate.get(index);
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
