package realCodes;

import util.DomainBoundary;
import util.Testcase;

import java.lang.reflect.Type;

public class RealCodesDriver {

    private Class origin = null;

    private Class mutant = null;

    RealCodesDriver(Class origin, Class mutant) {
        this.origin = origin;
        this.mutant = mutant;
    }

    public DomainBoundary getParamInfo() {
        Class<?>[] paramTypes = origin.getMethods()[0].getParameterTypes();
        return null;
    }

    public boolean isCorrect(Testcase testcase) {
        return false;
    }
}
