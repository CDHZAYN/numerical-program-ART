package art;

        import java.util.*;

        import faultZone.FaultZone;
        import faultZone.FaultZone_Point_Square;
        import util.*;

/**
 * MCMC（2013）
 * 论文：Enhancing Performance of Random Testing through Markov Chain Monte Carlo Methods
 * 大致方法：
 * 1. 随机生成一个测试用例进行测试
 * 2. 选择一个新的用例，生成一个随机数U，如果U小于等于可接受的可能值，认为新用例是有效的，反之则重新选择用例
 */

public class MCMC_ART extends AbstractART{

    public MCMC_ART(DomainBoundary inputBoundary) {
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
            MCMC_ART mcmc_art = new MCMC_ART(bd);
            //小run一下
            temp = mcmc_art.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("第" + i + "次试验F_Measure：" + temp);
            sums += temp;
        }

        System.out.println("MCMC_ART当前参数：dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate);
        System.out.println("Fm: " + sums / (double) times + "  且最后的Fart/Frt: "
                + sums / (double) times * failrate);// 平均每次使用的测试用例数
    }

    @Override
    public Testcase bestCandidate() {
        this.candidate.clear();
        Double fail_rate = 0.005;
        ArrayList<Testcase> cases = new ArrayList<>();
        double rand;
        Testcase tc = new Testcase(inputBoundary);
        do{
            tc = new Testcase(inputBoundary);
            rand = Math.random();
        }while(rand < 1.0 - fail_rate);
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