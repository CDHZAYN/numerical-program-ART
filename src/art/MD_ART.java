package art;

import faultZone.FaultZone;
import faultZone.FaultZone_Point_Square;
import util.DomainBoundary;
import util.Parameters;
import util.Testcase;

import java.util.ArrayList;
import java.util.Collections;

//TODO: 算法介绍
/**
 * 此处附上对该算法的论文、年份、内容等说明。例：
 * MD_ART（2022）
 * 论文：A Test Case Generation Method Based on Adaptive Random Testing and Metamorphic Relation
 * 大致方法：
 * 1.随机生成一个testcase e 再通过变异关系生成 e'
 * 2.将e和e'放入E
 * 3.随机生成多个测试用例，并得到对应变异测试用例，计算与E最短距离
 * 4.返回相应测试用例
 */
public class MD_ART extends AbstractART{

    private DomainBoundary inputBoundary;

    public MD_ART(DomainBoundary inputBoundary){
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
            MD_ART  md_art = new MD_ART(bd);
            //小run一下
            temp = md_art.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("第" + i + "次试验F_Measure：" + temp);
            sums += temp;
        }

        System.out.println("MD_ART当前参数：dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate);
        System.out.println("Fm: " + sums / (double) times + "  且最后的Fart/Frt: "
                + sums / (double) times * failrate);// 平均每次使用的测试用例数
    }
    public Testcase MR(Testcase tc){
        for(int x=0;x<tc.list.size();x++){
            double tmp = tc.getValue(x);
            tc.setValue(x,tmp);
        }
        return tc;
    }
    public double getDistance(Testcase a,ArrayList<Testcase>b){
        double distance = 0;
        for(int i=0;i<b.size();i++){
            double distance_of_i = Testcase.Distance(a,b.get(i));
            distance += distance_of_i;
        }
        return distance/b.size();
    }
    @Override
    public Testcase bestCandidate() {
        this.candidate.clear();
        this.candidate = Testcase.generateCandates(10,inputBoundary.getList());//候选testcase
        ArrayList<Testcase> E = this.total;
        if( E.size() == 0){
            Testcase tc = new Testcase(inputBoundary);
            Testcase new_tc = MR(tc);
            E.add(tc);
            E.add(new_tc);
        }
        double min_dis = Double.MAX_VALUE;
        Testcase aim_testcase = null;
        for(int i=0;i<this.candidate.size();i++){
            Testcase tc_i = this.candidate.get(i);
            Testcase newtc_i = MR(tc_i);
            if(getDistance(tc_i,E)<min_dis){
                min_dis = getDistance(tc_i,E);
                aim_testcase = tc_i;
            }
            if(getDistance(newtc_i,E)<min_dis){
                min_dis = getDistance(newtc_i,E);
                aim_testcase = newtc_i;
            }
        }
        return aim_testcase;
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
