package realCodes;

import util.Testcase;

public class RealCodeRunner implements Runnable {

    private Testcase testcase;

    private RealCodesDriver realCodesDriver;

    private boolean isCorrect = false;

    public RealCodeRunner(Testcase testcase, RealCodesDriver realCodesDriver) {
        this.testcase = testcase;
        this.realCodesDriver = realCodesDriver;
    }

    @Override
    public void run() {
        isCorrect = realCodesDriver.isCorrect(testcase);
    }

    public boolean getIsCorrect() {
        return isCorrect;
    }
}
