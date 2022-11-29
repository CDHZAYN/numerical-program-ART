package realCodes;

import com.sun.beans.editors.ByteEditor;
import util.Dimension;
import util.DomainBoundary;
import util.Parameters;
import util.Testcase;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class RealCodesDriver {

    private static String originPackage = "realCodes.original";

    private static String mutantPackage = "realCodes.mutant";

    private String realCodeName = null;

    private Class origin = null;

    private Class mutant = null;


    public RealCodesDriver(String realCodeName) throws ClassNotFoundException {
        this.realCodeName = realCodeName;
        origin = Class.forName(originPackage + "." + realCodeName);
        mutantPackage += "." + realCodeName;
    }

//    public RealCodesDriver(Class origin, Class mutant) {
//        this.origin = origin;
//        this.mutant = mutant;
//    }

    public int getMutantCodeNum() {
        File mutantdirFile = new File(Parameters.realCodesPath + "/mutant/" + realCodeName);
        File[] mutantFileNames = mutantdirFile.listFiles();
        return mutantFileNames.length;
    }

    public void setMutant(int index) throws ClassNotFoundException {
        File mutantdirFile = new File(Parameters.realCodesPath + "/mutant/" + realCodeName);
        File[] mutantFileNames = mutantdirFile.listFiles();
        String mutantClassName = mutantFileNames[index].getName();
        mutantClassName = mutantClassName.substring(0, mutantClassName.length() - 5);
        mutant = Class.forName(mutantPackage + "." + mutantClassName);
    }

    public DomainBoundary getDomainBoundary() {
        ArrayList<Dimension> paramDim = new ArrayList<>();
        if (realCodeName.equals("Bessj")) {
            paramDim.add(new Dimension(2, Parameters.defaultDimensionMax));
            paramDim.add(new Dimension(Parameters.defaultDimensionMin, Parameters.defaultDimensionMax));
//            } else if (realCodeName.equals("BubbleSort")) {
        } else if (realCodeName.equals("Encoder")) {
            for (int i = 0; i < Parameters.listParamNum; ++i)
                paramDim.add(new Dimension(Byte.MIN_VALUE, Byte.MAX_VALUE));
        } else if (realCodeName.equals("Expint")) {
            paramDim.add(new Dimension(0, Parameters.defaultDimensionMax));
            paramDim.add(new Dimension(0, Parameters.defaultDimensionMax));
        } else if (realCodeName.equals("Fisher")) {
            for (int i = 0; i < 3; ++i)
                paramDim.add(new Dimension(Parameters.defaultDimensionMin, Parameters.defaultDimensionMax));
        } else if (realCodeName.equals("Gammq")) {
            paramDim.add(new Dimension(0, Parameters.defaultDimensionMax));
            paramDim.add(new Dimension(0.01, Parameters.defaultDimensionMax));
        } else if (realCodeName.equals("Median")) {
            for (int i = 0; i < Parameters.listParamNum; ++i)
                paramDim.add(new Dimension(Parameters.defaultDimensionMin, Parameters.defaultDimensionMax));
        } else if (realCodeName.equals("Remainder")) {
            for (int i = 0; i < 2; ++i)
                paramDim.add(new Dimension(Parameters.defaultDimensionMin, Parameters.defaultDimensionMax));
        } else if (realCodeName.equals("Triangle")) {
            for (int i = 0; i < 3; ++i)
                paramDim.add(new Dimension(0, Parameters.defaultDimensionMax));
        } else if (realCodeName.equals("Triangle2")) {
            for (int i = 0; i < 3; ++i)
                paramDim.add(new Dimension(0, Parameters.defaultDimensionMax));
        } else if (realCodeName.equals("Variance")) {
            for (int i = 0; i < Parameters.listParamNum; ++i)
                paramDim.add(new Dimension(Parameters.defaultDimensionMin, Parameters.defaultDimensionMax));
        }
        return new DomainBoundary(paramDim);
    }

    public double[] generateDoubleListParam(Testcase testcase, int start) {
        double[] rst = new double[Parameters.listParamNum];
        for (int i = 0; i < Parameters.listParamNum; ++i)
            rst[i] = testcase.getValue(start + i);
        return rst;
    }

    public int[] generateIntListParam(Testcase testcase, int start) {
        int[] rst = new int[Parameters.listParamNum];
        for (int i = 0; i < Parameters.listParamNum; ++i)
            rst[i] = (int) testcase.getValue(start + i);
        return rst;
    }

    public boolean isCorrect(Testcase testcase) {
        boolean rst = false;
        try {
            Method originMethod = origin.getMethods()[0];
            Object originObject = origin.newInstance();
            double originResult = 0;

            Method mutantMethod = mutant.getMethods()[0];
            Object mutantObject = mutant.newInstance();
            double mutantResult = 0;

            if (realCodeName.equals("Bessj")) {
                originResult = (double) originMethod.invoke(originObject, (int) testcase.getValue(0), (double) testcase.getValue(1));
                mutantResult = (double) mutantMethod.invoke(mutantObject, (int) testcase.getValue(0), (double) testcase.getValue(1));
//            } else if (realCodeName.equals("BubbleSort")) {
//                originMethod.invoke(originObject, generateIntListParam(testcase, 0));
//                mutantMethod.invoke(mutantObject, generateIntListParam(testcase, 0));
            } else if (realCodeName.equals("Encoder")) {
                String originResultStr = (String) originMethod.invoke(originObject, generateIntListParam(testcase, 0));
                String mutantResultStr = (String) mutantMethod.invoke(mutantObject, generateIntListParam(testcase, 0));
                if (originResultStr.equals(mutantResultStr))
                    return true;
            } else if (realCodeName.equals("Expint")) {
                originResult = (double) originMethod.invoke(originObject, (int) testcase.getValue(0), (double) testcase.getValue(1));
                mutantResult = (double) mutantMethod.invoke(mutantObject, (int) testcase.getValue(0), (double) testcase.getValue(1));
            } else if (realCodeName.equals("Fisher")) {
                originResult = (double) originMethod.invoke(originObject, (int) testcase.getValue(0), (int) testcase.getValue(1), (double) testcase.getValue(2));
                mutantResult = (double) mutantMethod.invoke(mutantObject, (int) testcase.getValue(0), (int) testcase.getValue(1), (double) testcase.getValue(2));
            } else if (realCodeName.equals("Gammq")) {
                originResult = (double) originMethod.invoke(originObject, (double) testcase.getValue(0), (double) testcase.getValue(1));
                mutantResult = (double) mutantMethod.invoke(mutantObject, (double) testcase.getValue(0), (double) testcase.getValue(1));
            } else if (realCodeName.equals("Median")) {
                originResult = (double) originMethod.invoke(originObject, generateIntListParam(testcase, 0));
                mutantResult = (double) mutantMethod.invoke(mutantObject, generateIntListParam(testcase, 0));
            } else if (realCodeName.equals("Remainder")) {
                originResult = (double) originMethod.invoke(originObject, (int) testcase.getValue(0), (int) testcase.getValue(1));
                mutantResult = (double) mutantMethod.invoke(mutantObject, (int) testcase.getValue(0), (int) testcase.getValue(1));
            } else if (realCodeName.equals("Triangle")) {
                originResult = (double) originMethod.invoke(originObject, (int) testcase.getValue(0), (int) testcase.getValue(1), (int) testcase.getValue(2));
                mutantResult = (double) mutantMethod.invoke(mutantObject, (int) testcase.getValue(0), (int) testcase.getValue(1), (int) testcase.getValue(2));
            } else if (realCodeName.equals("Triangle2")) {
                originResult = (double) originMethod.invoke(originObject, (int) testcase.getValue(0), (int) testcase.getValue(1), (int) testcase.getValue(2));
                mutantResult = (double) mutantMethod.invoke(mutantObject, (int) testcase.getValue(0), (int) testcase.getValue(1), (int) testcase.getValue(2));
            } else if (realCodeName.equals("Variance")) {
                originResult = (double) originMethod.invoke(originObject, generateIntListParam(testcase, 0));
                mutantResult = (double) mutantMethod.invoke(mutantObject, generateIntListParam(testcase, 0));
            }
            if (originResult == mutantResult)
                rst = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rst;
    }
}
