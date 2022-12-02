package art;

import java.util.*;

import faultZone.FaultZone;
import faultZone.FaultZone_Point_Square;
import util.*;

/**
 * RP_ART（2004）
 * 论文：Adaptive Random Testing Through Dynamic Partitioning
 * 大致方法：
 * 1. 首先测试随机生成的用例1，并生成候选用例组
 * 2. 找到距离1最远的距离2，并通过随机数对候选域进行随机划分
 */

public class RP_ART extends AbstractART{

    //����������ʼ�����㷨
    public RP_ART(DomainBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
        partitionTree = new PartitionTree(inputBoundary);
    }
    public PartitionTree partitionTree;

    public static void main(String args[]){
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
            RP_ART RP_art = new RP_ART(bd);
            //Сrunһ��
            temp = RP_art.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("��" + i + "������F_Measure��" + temp);
            sums += temp;
        }

        System.out.println("RP_ART��ǰ������dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate);
        System.out.println("Fm: " + sums / (double) times + "  ������Fart/Frt: "
                + sums / (double) times * failrate);// ƽ��ÿ��ʹ�õĲ���������
    }

    @Override
    public Testcase bestCandidate() {
        candidate.clear();
        List<PartitionTree> partitionLeaves = partitionTree.getLeaveTreeNodes();
        double maxSize = partitionLeaves.get(0).getSize();
        PartitionTree maxSizeLeaf = partitionLeaves.get(0);
        for (PartitionTree leaf : partitionLeaves) {
            if (maxSize < leaf.getSize()) {
                maxSize = leaf.getSize();
                maxSizeLeaf = leaf;
            }
        }
        //����10�������������ҵ����Ѳ��Ծ������Ĳ���������
        candidate = Testcase.generateCandates(10, maxSizeLeaf.getDomainBoundary().getList());
        if (maxSizeLeaf.gettestcaseList().isEmpty()) {
            partitionTree.addTestcase(candidate.get(0));
            return candidate.get(0);
        }
        Testcase testedTestcase = maxSizeLeaf.gettestcaseList().get(0);
        double maxDistance = 0;
        Testcase maxDistanceTestcase = null;
        for (Testcase testcase : candidate) {
            double distance = Testcase.Distance(testcase, testedTestcase);
            if (distance > maxDistance) {
                maxDistance = distance;
                maxDistanceTestcase = testcase;
            }
        }
        partitionTree.addTestcase(maxDistanceTestcase);

        //�����������
        Testcase divide = new Testcase();
        Random r = new Random();
        int r1 = r.nextInt(5);
        for (int i = 0; i < inputBoundary.dimensionOfInputDomain(); ++i) {
            divide.list.add(((double) ( maxDistanceTestcase.getValue(i))) * Math.random());
        }
        maxSizeLeaf.partition(2, divide);
        return maxDistanceTestcase;
    }

    @Override
    public void testEfficiency(int pointNum) {
        Testcase testcase = new Testcase(inputBoundary);
        while (total.size() < pointNum) {
            total.add(testcase);
            testcase = bestCandidate();
        }
    }
}