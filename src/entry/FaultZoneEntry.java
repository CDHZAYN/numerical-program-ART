package entry;

import art.AbstractART;
import faultZone.FaultZone;
import faultZone.FaultZone_Block;
import faultZone.FaultZone_Point_Square;
import faultZone.FaultZone_Strip;
import realCodes.RealCodesDriver;
import util.DomainBoundary;
import util.Parameters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class FaultZoneEntry extends AbstractEntry {

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        FaultZoneEntry faultZoneEntry = new FaultZoneEntry();
        Class ARTClass = Class.forName("art." + testingART);

        System.out.println("start testing.");
        System.out.println("now testing efficiency.");

        DomainBoundary domainBoundary = new DomainBoundary(Parameters.defaultDimensionNum, Parameters.defaultDimensionMin, Parameters.defaultDimensionMax);
        double faultZoneMean = faultZoneEntry.testEfficiency(ARTClass, domainBoundary, Parameters.efficiencyTestcaseNum);
        System.out.println("Efficiency: " + faultZoneMean);

        System.out.println("now testing effectiveness.");
        double[] failrates = new double[]{0.2, 0.1, 0.05};
        for(int i = 0; i < failrates.length; ++i) {
            System.out.println("testing: Block-"+ failrates[i]);
            FaultZone faultZone = new FaultZone_Block(domainBoundary, failrates[i]);
            double realCodeMean = faultZoneEntry.testEffectiveness(ARTClass, domainBoundary, faultZone);
            System.out.println("Effectiveness: " + realCodeMean);
            faultZoneEntry.addResult(realCodeMean);
        }
        for(int i = 0; i < failrates.length; ++i) {
            System.out.println("testing: Point_Square-"+ failrates[i]);
            FaultZone faultZone = new FaultZone_Point_Square(domainBoundary, failrates[i]);
            double realCodeMean = faultZoneEntry.testEffectiveness(ARTClass, domainBoundary, faultZone);
            System.out.println("Effectiveness: " + realCodeMean);
            faultZoneEntry.addResult(realCodeMean);
        }
        for(int i = 0; i < failrates.length; ++i) {
            System.out.println("testing: Strip-"+ failrates[i]);
            FaultZone faultZone = new FaultZone_Strip(domainBoundary, failrates[i]);
            double realCodeMean = faultZoneEntry.testEffectiveness(ARTClass, domainBoundary, faultZone);
            System.out.println("Effectiveness: " + realCodeMean);
            faultZoneEntry.addResult(realCodeMean);
        }

        System.out.println("now storing result.");
        faultZoneEntry.storeResult(Parameters.faultZoneResultDir + "/" + testingART + ".txt");
    }

    @Override
    public double testEffectiveness(Class<? extends AbstractART> ART, DomainBoundary inputBoundary, Object faultZone) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        int temp = 0;
        int timeSum = 0;
        int cannotFindNum = 0;
        Constructor constructor = ART.getConstructor(DomainBoundary.class);
        for (int i = 1; i <= Parameters.testRoundNum; i++) {
            AbstractART fscs_block = (AbstractART) constructor.newInstance(inputBoundary);
            temp = fscs_block.runWithFaultZone((FaultZone) faultZone);
            if (temp == Parameters.maxAttemptNum)
                ++cannotFindNum;
            if (cannotFindNum == 10)
                return Parameters.maxAttemptNum;
            timeSum += temp;
        }

        return (double)timeSum / (double)Parameters.testRoundNum;
    }
}