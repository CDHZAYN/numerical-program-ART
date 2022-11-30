package art;

import faultZone.PartitionTree;
import util.DomainBoundary;
import util.Testcase;

/**
 * TPP—ART（2011）
 * 论文：Adaptive Random Testing Based on Two-Point Partitioning
 * 大致方法：
 * 1、在当前最大的划分CurReg中，生成多个候选输入；
 * 2、如果CurReg中没有已测试输入，则直接进行测试；
 * 3、如果CurReg中已有测试输入T1，找到与T1距离最远的候选输入T2；
 * 4、若找不到错误，则CurReg以T1与T2的中点为参照点划分，找到新的最大划分，从1开始。
 */
public class TPP_ART extends AbstractART {

    private PartitionTree partitionTree;

    public TPP_ART(DomainBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    public static void main(String args[]) {
        //TODO:能单独运行该算法的入口

        // 建议统一使用failrate为0.05的faultzone_point_square，参照FSCS_ART
    }

    @Override
    public Testcase bestCandidate() {
        //TODO:找到下一个测试用例的方法
        return null;
    }

    @Override
    public void testEfficiency(int pointNum) {
        //TODO:生成指定数量测试用例的方法
    }
}
