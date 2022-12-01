package entry;

import art.AbstractART;
import realCodes.RealCodesDriver;
import util.DomainBoundary;
import util.Parameters;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class RealCodeEntry extends AbstractEntry {

    private static String testingCodeName = "Remainder";

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        RealCodeEntry realCodeEntry = new RealCodeEntry();
        Class ARTClass = Class.forName("art." + testingART);

        System.out.println("start testing.");
        System.out.println("now testing efficiency.");

        DomainBoundary domainBoundary = new DomainBoundary(Parameters.defaultDimensionNum, Parameters.defaultDimensionMin, Parameters.defaultDimensionMax);
        double efficiencyMean = realCodeEntry.testEfficiency(ARTClass, domainBoundary, Parameters.efficiencyTestcaseNum);
        System.out.println("Efficiency: " + efficiencyMean);
        realCodeEntry.addResult(efficiencyMean);

        System.out.println("now testing effectiveness.");

        RealCodesDriver realCodesDriver = new RealCodesDriver(testingCodeName);
        int mutantNum = realCodesDriver.getMutantCodeNum();
        int step = mutantNum / 50;
        for (int i = 0; i < mutantNum; i += step) {
            System.out.println("testing: " + i);
            realCodesDriver.setMutant(i);
            double EffectivenessMean = realCodeEntry.testEffectiveness(ARTClass, realCodesDriver.getDomainBoundary(), realCodesDriver);
            System.out.println("Effectiveness: " + EffectivenessMean);
            realCodeEntry.addResult(EffectivenessMean);
        }
        System.out.println("now storing result.");
        realCodeEntry.storeResult(Parameters.realCodeResultDir + "/" + testingART + "-" + testingCodeName + ".txt");
    }

    @Override
    public double testEffectiveness(Class<? extends AbstractART> ARTClass, DomainBoundary inputBoundary, Object realCodesDriver) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        int timeSum = 0;
        int temp = 0;
        Constructor constructor = ARTClass.getConstructor(DomainBoundary.class);
        int cannotFindNum = 0;
        for (int i = 1; i <= Parameters.testRoundNum; i++) {
            AbstractART ART = (AbstractART) constructor.newInstance(inputBoundary);
            temp = ART.runWithNumericProgram((RealCodesDriver) realCodesDriver);
            if (temp < 0)
                return temp * -1;
            if (temp == Parameters.maxAttemptNum)
                ++cannotFindNum;
            if (cannotFindNum == 10)
                return Parameters.maxAttemptNum;
            timeSum += temp;
        }

        return (double) timeSum / (double) Parameters.testRoundNum;
    }
}
