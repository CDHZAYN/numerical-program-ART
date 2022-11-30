package art;

import util.Testcase;

/**
 * MART（2011）
 * 论文：Mirror adaptive random testing
 * 大致方法：
 * 1、按指定数目将输入空间平均划分，指定其中一个划分为主划分，其他划分为镜像划分。
 * 2、在主划分中随机生成一个测试用例，在每个镜像划分中以平移、镜像方法生成其他多个测试用例。
 */
public class MART extends AbstractART{

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
