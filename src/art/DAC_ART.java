package art;

import util.Testcase;

/**
 * DAC—ART（2013 13th International Conference on Quality Software）
 * 论文：The ART of Divide and Conquer
 * 大致方法：
 * 1、平均划分输入空间，
 * 2、找到其中已测试输入最少的划分进行测试，
 * 3、不断循环2直到每个划分中的已测试输入均达到指定数量。
 * 4、再次平均划分每个划分。
 */
public class DAC_ART extends AbstractART{

    public static void main(String args[]){
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
