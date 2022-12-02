package art;

import java.util.ArrayList;
import java.util.Random;

import faultZone.FaultZone;
import faultZone.FaultZone_Point_Square;
import util.*;

/**
 * FSCS_SIMD 代码实现
 * 论文：An efficient implementation of Fixed-Size-Candidate-Set adaptive random testing using SIMD instructions()
 * 具体方法：
 * 1.计算测试用例之间距离
 * 2.得到每行的最短距离
 * 3.得到最短距离的最大值，返回相应的测试用例
 */
public class SIMD_FSCS_ART extends AbstractART {


    //用输入来初始化该算法
    public SIMD_FSCS_ART(DomainBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }
    double[][] getDismatrix(ArrayList<Testcase> C,ArrayList<Testcase>E){
        int n = C.size();
        int m = E.size();
        double[][] dismatrix = new double[n][m];
        for(int x=0;x<n;x++){
            for(int y=0;y<m;y++){
                dismatrix[x][y] = Testcase.Distance(C.get(x),E.get(y));
            }
        }
        return dismatrix;
    }
    //找到在输入空间中，距离其他输入最远的输入，将其作为下一个用于测试的输入返回
    @Override
    public Testcase bestCandidate() {
        //randomly generate 10 testcase
        this.candidate.clear();
        this.candidate = Testcase.generateCandates(10, inputBoundary.getList());
        double[][] disMatrix = getDismatrix(this.candidate,this.total);
        double minDistance = Double.MIN_VALUE;
        //如果之前没有进行过候选输入选择，则随机生成一个输入当作找到了
        if (total.size() == 0) {
            return candidate.get(new Random().nextInt(candidate.size()));
        }
        int index_of_x = 0;
        int index_of_y = 0;
        int ret = 0;
        double min = Double.MAX_VALUE;
        for(int x=0;x<disMatrix.length;x++){
            for(int y=0;y<disMatrix[x].length;y++){
                if(disMatrix[x][y]<min){
                    index_of_x = x;
                    index_of_y = y;
                    min = disMatrix[x][y];
                }
            }
            if(minDistance<min){
                minDistance = min;
                ret = index_of_x;
            }
        }
        return this.candidate.get(ret);
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
            SIMD_FSCS_ART simd_fscs_art = new SIMD_FSCS_ART(bd);
            //小run一下
            temp = simd_fscs_art.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("第" + i + "次试验F_Measure：" + temp);
            sums += temp;
        }

        System.out.println("SIMD_FSCS_ART当前参数：dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate); //输出当前参数信息
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
            candidate = new ArrayList<Testcase>();
            for (int i = 0; i < 10; i++) {
                candidate.add(new Testcase(inputBoundary));
            }
            testcase = bestCandidate();
        }
    }

}
