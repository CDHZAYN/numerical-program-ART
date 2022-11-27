package fscs;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.tools.Tool;

import faultZone.FaultZone;
import faultZone.FaultZone_Block;
import faultZone.FaultZone_Point_Square;
import faultZone.FaultZone_Strip;
import model.*;

/**
 * FSCS 代码实现
 * 论文：2004-ASIAN-Adaptive random testing
 */
public class FSCS_art extends AbstractART {

    public DomainBoundary inputBoundary = new DomainBoundary();
    int count = 1;

    Double p;

    public FSCS_art(DomainBoundary inpuBoundary, Double p) {
        this.inputBoundary = inpuBoundary;
        this.p = p;
    }

    public FSCS_art() {

    }

    @Override
    public Testcase Best_candidate() {
        this.Candidate.clear();
        this.Candidate = Testcase.generateCandates(10, inputBoundary.getList());

        Testcase p = null;
        double mindist, maxmin = 0;
        int cixu = -1;
        if (total.size() == 0) {
            return Candidate.get(new Random().nextInt(Candidate.size()));
        }
        for (int i = 0; i < this.Candidate.size(); i++) {
            mindist = Double.MAX_VALUE;
            for (int j = 0; j < this.total.size(); j++) {
                double dist = Testcase.Distance(this.Candidate.get(i), this.total.get(j), this.p);
                if (dist < mindist) {
                    mindist = dist;
                }
            }
            if (maxmin < mindist) {
                maxmin = mindist;
                cixu = i;
            }
        }

        return this.Candidate.get(cixu);
    }


    public static void main(String args[]) {
        int times = 3000;
        long sums = 0;// 初始化使用的测试用例数
        int temp = 0;// 初始化测试用例落在失效域的使用的测试用例的个数

        ArrayList<Integer> result = new ArrayList<>();

        double p = Parameters.lp;
        double failrate = 0.005;
        int dimension = 2;
        DomainBoundary bd = new DomainBoundary(dimension, -5000, 5000);

        for (int i = 1; i <= times; i++) {
            FaultZone fz = new FaultZone_Point_Square(bd, failrate);
            FSCS_art fscs_block = new FSCS_art(bd, p);
            temp = fscs_block.run(fz);
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
            testcase = Best_candidate();
        }
    }

}
