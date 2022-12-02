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
public class MCMC_ART extends AbstractART {

    public MCMC_ART(DomainBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    public static void main(String args[]) {
        int times = 3000;//һ��ִ�У����㷨���ظ�3000��
        long sums = 0;// ��ʼ��ʹ�õĲ���������
        int temp = 0;// ��ʼ��������������ʧЧ���ʹ�õĲ��������ĸ���

        ArrayList<Integer> result = new ArrayList<>();

        //ͳһp=2����ʾ�����������밴�����������㣨��ά�Ⱦ���ƽ���Ϳ�ƽ����
        double p = Parameters.lp;
        double failrate = 0.005;
        int dimension = 2;
        //��ά���루��һ������������������������ֵ���������ޣ�5000�������ޣ�5000����ͬ
        DomainBoundary bd = new DomainBoundary(dimension, -5000, 5000);

        for (int i = 1; i <= times; i++) {
            //ָ��ʹ������fault zone
            FaultZone fz = new FaultZone_Point_Square(bd, failrate);
            MCMC_ART mcmc_art = new MCMC_ART(bd);
            //Сrunһ��
            temp = mcmc_art.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("��" + i + "������F_Measure��" + temp);
            sums += temp;
        }

        System.out.println("MCMC_ART��ǰ������dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate);
        System.out.println("Fm: " + sums / (double) times + "  ������Fart/Frt: "
                + sums / (double) times * failrate);// ƽ��ÿ��ʹ�õĲ���������
    }

    @Override
    public Testcase bestCandidate() {
        this.candidate.clear();
        Double fail_rate = 0.005;
        ArrayList<Testcase> cases = new ArrayList<>();
        double rand;
        Testcase tc = new Testcase(inputBoundary);
        do {
            tc = new Testcase(inputBoundary);
            rand = Math.random();
        } while (rand < 1.0 - fail_rate);
        return tc;
    }

    @Override
    public void testEfficiency(int pointNum) { // ����Ч�ʲ���
        Testcase testcase = new Testcase(inputBoundary);
        while (total.size() < pointNum) { // �������n����ѡ�Ĳ�������
            total.add(testcase);
            candidate = new ArrayList<Testcase>();
            for (int i = 0; i < 10; i++) {
                candidate.add(new Testcase(inputBoundary));
            }
            testcase = bestCandidate();
        }
    }
}