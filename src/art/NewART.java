package art;

import util.DomainBoundary;
import util.Testcase;

//TODO: 算法介绍
/**
* 此处附上对该算法的论文、年份、内容等说明。例：
* NewART（2022）
* 论文： The Art of ART
* 大致方法：将输入空间划分，每个小输入空间的已测试输入达到指定数量时，再次对每一个小输入空间划分。
 */
public class NewART extends AbstractART{

    private DomainBoundary inputBoundary;

    public NewART(DomainBoundary inputBoundary){
        this.inputBoundary = inputBoundary;
    }

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
