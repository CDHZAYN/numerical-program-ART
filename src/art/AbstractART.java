package art;

import faultZone.FaultZone;
import realCodes.RealCodesDriver;
import util.Testcase;

import java.util.ArrayList;

public abstract class AbstractART {
    public ArrayList<Testcase> total = new ArrayList<>();
    public ArrayList<Testcase> Candidate = new ArrayList<>();
    int count=0;


    //使用fault zone方法验证效率和可用性
    public int runWithFaultZone(FaultZone fz) {
        Testcase testcase=null;
        //先找到一个输入判断
        do{
            testcase = bestCandidate();
            count++;
            total.add(testcase);
        }while (fz.isCorrect(testcase));
        return count;
    }

    //使用真实的数值程序验证效率和可用性
    public int runWithNumericProgram(RealCodesDriver rcz) {
        Testcase testcase=null;
        //先找到一个输入判断
        do{
            testcase = bestCandidate();
            count++;
            total.add(testcase);
        }while (rcz.isCorrect(testcase));
        return count;
    }

    public abstract Testcase bestCandidate();

    public abstract void testEfficiency(int pointNum);


}



