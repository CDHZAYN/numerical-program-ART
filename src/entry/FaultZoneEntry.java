package entry;

import art.AbstractART;
import faultZone.FaultZone;
import faultZone.FaultZone_Block;
import faultZone.FaultZone_Point_Square;
import faultZone.FaultZone_Strip;
import util.DomainBoundary;
import util.Parameters;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class FaultZoneEntry extends AbstractEntry {

    @Override
    public ArrayList<Integer> testEffectiveness(Class<? extends AbstractART> ART, DomainBoundary inputBoundary, Object faultZone) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ArrayList<Integer> result = new ArrayList<>();
        int temp = 0;
        Constructor constructor = ART.getConstructor(DomainBoundary.class);
        for (int i = 1; i <= Parameters.testRoundNum; i++) {
            AbstractART fscs_block = (AbstractART) constructor.newInstance(inputBoundary);
            temp = fscs_block.run((FaultZone) faultZone);
            result.add(temp);
        }

        return result;
    }
}