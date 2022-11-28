package entry;

import art.AbstractART;
import art.FSCS_ART;
import util.DomainBoundary;
import util.Parameters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractEntry {

    //TODO: 把新增的class添入list里
    List<Class<? extends AbstractART>> ARTs = new ArrayList<>(Arrays.asList(
            FSCS_ART.class
    ));

    public ArrayList<Long> testEfficiency(Class<? extends AbstractART> art, DomainBoundary inputBoundary, int testcaseNum) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        FSCS_ART fscs;
        ArrayList<Long> result = new ArrayList<>();
        Constructor constructor = art.getConstructor(DomainBoundary.class);
        for (int i = 0; i < Parameters.testRoundNum; i++) {
            AbstractART certainArt = (AbstractART) constructor.newInstance(inputBoundary);
            long n1 = System.currentTimeMillis();
            certainArt.testEfficiency(testcaseNum);
            long n2 = System.currentTimeMillis();
        }
        return result;
    }

    public abstract ArrayList<Integer> testEffectiveness(Class<? extends AbstractART> ART, DomainBoundary inputBoundary, Object faultZoneOrRealCode) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

}
