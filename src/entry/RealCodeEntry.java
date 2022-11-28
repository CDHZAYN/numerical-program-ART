package entry;

import art.AbstractART;
import util.DomainBoundary;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class RealCodeEntry extends AbstractEntry{
    @Override
    public ArrayList<Integer> testEffectiveness(Class<? extends AbstractART> ART, DomainBoundary inputBoundary, Object realCode) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return null;
    }
}
