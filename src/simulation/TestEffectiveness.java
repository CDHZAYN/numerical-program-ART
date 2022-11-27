package simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import faultZone.*;
import fscs.FSCS_art;
import model.*;

import javax.swing.*;

public class TestEffectiveness {

    final static double lp = Parameters.lp;
    final static int times = 30; // The times to run experiments
    final static int thread_pool_num = Parameters.thread_pool_num;
    final static double R = Parameters.R;


    public static void main(String args[]) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<? extends AbstractART> algorithm = FSCS_art.class;
        String basePath = "./output1/";
        File filedir = new File(basePath + algorithm.getName());
        if (!filedir.exists()) {
            filedir.mkdirs();
        }
        double[] areas = new double[]{0.01, 0.005, 0.002, 0.001}; // failure rate
        int[] dimensions = new int[]{2};  // dimension of inputdomain
        for (int dim : dimensions) {
            for (double area : areas) {
                DomainBoundary bd = new DomainBoundary(dim, -5000, 5000);
                String s1 = basePath + new StringBuffer(algorithm.getName() + "\\").append(dim).append("d-Block-").append(area).append(".txt");
                String s2 = basePath + new StringBuffer(algorithm.getName() + "\\").append(dim).append("d-Strip-").append(area).append(".txt");
                String s3 = basePath + new StringBuffer(algorithm.getName() + "\\").append(dim).append("d-Point-").append(area).append(".txt");

                test(bd, area, 1, s1, FSCS_art.class);
                test(bd, area, 2, s2, FSCS_art.class);
                test(bd, area, 3, s3, FSCS_art.class);
            }
        }
        System.exit(0);
    }


    /**
     * @param inputBoundary The inputdomain in Simulations.
     * @param failrate      The failrate used to generate the failure pattern.
     * @param faultZoneFlag 1: Block failure region; 2: Strip failure region; 3: Point failure region;
     * @param filePath      The results will be stored in this filepath
     * @param methodName    The method to generate test cases
     */

    public static void test(DomainBoundary inputBoundary, double failrate, int faultZoneFlag, String filePath, Class method) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        int dimension = inputBoundary.dimensionOfInputDomain();
        long sumsRRT = 0;
        ArrayList<Integer> FmeasureResults = new ArrayList<>();
        int temp = 0;
        FaultZone fz = null;
        switch (faultZoneFlag) {
            case 1:
                fz = new FaultZone_Block(inputBoundary, failrate);
                break;
            case 2:
                fz = new FaultZone_Strip(inputBoundary, failrate, 0.9);
                break;
            case 3:
                fz = new FaultZone_Point_Square(inputBoundary, failrate);
                break;
            default:
                break;
        }
        Constructor constructor = method.getConstructor(DomainBoundary.class, Double.class);
        for (int i = 1; i <= times; i++) {
            System.out.print(i);
            AbstractART fscs_block = (AbstractART) constructor.newInstance(inputBoundary, Parameters.lp);
            temp = fscs_block.run(fz);
            FmeasureResults.add(temp);
            System.out.println(new StringBuffer(i).append("_").append(temp).append("\t").toString());
            sumsRRT += temp;
        }

        double Fm = sumsRRT / (double) times;
        double Fr = sumsRRT / (double) times * failrate;
        System.out.println("\r\nDimension = " + dimension + "   failure-rate = " + failrate + "   faultFlag = " + faultZoneFlag);
        System.out.println("\tThe average of Fm=: " + Fm + "  Fart/Frt= " + Fr);


        try {

            java.io.File file2 = new java.io.File(filePath);
            FileWriter writeStream = new FileWriter(file2);
            writeStream.write(failrate + "\r\n" + Fm + "\r\n" + Fr + "\r\n" + "\r\n" + "\r\n");
            for (int j = 0; j < FmeasureResults.size(); j++) {
                writeStream.write(FmeasureResults.get(j) + "\r\n");
            }
            writeStream.flush();
            writeStream.close();
        } catch (IOException e) {

            System.out.println(e);
            try {
                System.out.println(String.format(filePath, method.getClass().getName()));
                Thread.sleep(30000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

    }

}
