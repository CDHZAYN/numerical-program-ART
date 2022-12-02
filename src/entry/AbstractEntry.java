package entry;

import art.AbstractART;
import art.FSCS_ART;
import util.DomainBoundary;
import util.Parameters;
import util.StoreResults;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractEntry {

    private ArrayList<Double> result = new ArrayList<>();

    static String testingART = "TPP_ART";

    public double testEfficiency(Class<? extends AbstractART> art, DomainBoundary inputBoundary, int testcaseNum) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        FSCS_ART fscs;
        Long timeSum = Long.valueOf(0);
        Constructor constructor = art.getConstructor(DomainBoundary.class);
        for (int i = 0; i < Parameters.testRoundNum; i++) {
            AbstractART certainArt = (AbstractART) constructor.newInstance(inputBoundary);
            long n1 = System.currentTimeMillis();
            certainArt.testEfficiency(testcaseNum);
            long n2 = System.currentTimeMillis();
            timeSum += n2 - n1;
        }
        return (double) timeSum / (double) Parameters.testRoundNum;
    }

    public void addResult(double part) {
        result.add(part);
    }

    public void storeResult(String filePath) {
        ArrayList<String> resultStr = new ArrayList<>();
        int testNum = result.size() - 1;
        int unfoundNum = 0;
        for (int i = 1; i < result.size(); ++i) {
            if(result.get(i).equals(1000.0)) {
                resultStr.add("UNFOUND");
                ++unfoundNum;
            } else
                resultStr.add(String.valueOf(result.get(i)));
        }
        resultStr.add("EFFICIENCY: "+ String.valueOf(result.get(0)));
        resultStr.add("TOTAL TEST NUM: " + String.valueOf(testNum));
        resultStr.add("ERROR NOT FOUND TEST NUM: " + String.valueOf(unfoundNum));
        resultStr.add("ERROR FOUND RATE: " + String.valueOf((double) (testNum - unfoundNum) / (double) testNum));
        StoreResults<String> storeResults = new StoreResults<>(filePath, resultStr);
        storeResults.run();
    }


    public abstract double testEffectiveness(Class<? extends AbstractART> ART, DomainBoundary inputBoundary, Object faultZoneOrRealCode) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;
}
