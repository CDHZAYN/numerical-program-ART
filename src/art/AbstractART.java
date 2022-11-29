package art;

import faultZone.FaultZone;
import realCodes.RealCodeRunner;
import realCodes.RealCodesDriver;
import util.Parameters;
import util.Testcase;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.*;

public abstract class AbstractART {
    public ArrayList<Testcase> total = new ArrayList<>();
    public ArrayList<Testcase> Candidate = new ArrayList<>();
    int count = 0;


    //使用fault zone方法验证效率和可用性
    public int runWithFaultZone(FaultZone fz) {
        Testcase testcase = null;
        //先找到一个输入判断
        do {
            testcase = bestCandidate();
            count++;
            if (count == Parameters.maxAttemptNum)
                break;
            total.add(testcase);
        } while (fz.isCorrect(testcase));
        return count;
    }

    //使用真实的数值程序验证效率和可用性
    public int runWithNumericProgram(RealCodesDriver rcz) {
        Testcase testcase = null;
        //先找到一个输入判断
        while(true) {
            testcase = bestCandidate();
            count++;
            if (count == Parameters.maxAttemptNum) {
                break;
            }
            total.add(testcase);

            boolean isCorrect = false;
            //关闭运行大于5s的程序
            ExecutorService exec = Executors.newFixedThreadPool(1);
            Testcase finalTestcase = testcase;
            Callable<Boolean> call = new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return rcz.isCorrect(finalTestcase);
                }
            };
            try {
                Future<Boolean> future = exec.submit(call);
                isCorrect = future.get(1000 * 5, TimeUnit.MILLISECONDS);
            } catch (TimeoutException | InterruptedException | ExecutionException ex) {
                System.out.println("unstoppable loop found.");
                return -1 * count;
            } catch(Exception e){
                System.out.println("incorrect exception triggered.");
                return count;
            }
            exec.shutdown();  // 关闭线程池

            if (!isCorrect)
                break;
        }
        return count;
    }

    public abstract Testcase bestCandidate();

    public abstract void testEfficiency(int pointNum);


}



