package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Testcase {
    public ArrayList<Double> list = new ArrayList<>();

    public Testcase() {
        list = new ArrayList<>();
    }

    /**
     * Random generate a test case according to the dim_list
     *
     * @param dim_list
     */
    public Testcase(ArrayList<Dimension> dim_list) {
        list = new ArrayList<>(dim_list.size());
        for (int i = 0; i < dim_list.size(); i++) {
            list.add(dim_list.get(i).getMin() + ((dim_list.get(i).getRange()) * new Random().nextDouble()));
        }
    }

    /**
     * @linkmethod TestCase(ArrayList < Dimension > dim_list)
     */
    public Testcase(DomainBoundary inputDomain) {
        ArrayList<Dimension> dim_list = inputDomain.BoundaryData;
        list = new ArrayList<>(dim_list.size());
        for (int i = 0; i < dim_list.size(); i++) {
            list.add(dim_list.get(i).getMin() + ((dim_list.get(i).getRange()) * Math.random()));
        }
    }

    /**
     * Generate the test case according to the String
     *
     * @param tcStr (XX,XX,XX)
     */
    public Testcase(String tcStr) {
        list = new ArrayList<>();
        if (tcStr.startsWith("(") && tcStr.endsWith(")")) {
            tcStr = tcStr.substring(1, tcStr.length() - 1);
        }
        String[] numbers = tcStr.split(",");
        for (String num : numbers) {
            list.add(Double.parseDouble(num));
        }
    }

    public Testcase(DomainBoundary inputDomain, Class<?>[] classees) {
        ArrayList<Dimension> dim_list = inputDomain.BoundaryData;
        for (int i = 0; i < dim_list.size(); i++) {
            double temp;
            if (classees[i] == int.class)
                temp = (int) (dim_list.get(i).getMin() + (dim_list.get(i).getRange() + 1) * Math.random());
            else if (classees[i] == float.class)
                temp = (float) (dim_list.get(i).getMin() + (dim_list.get(i).getRange()) * Math.random());
            else if (classees[i] == long.class)
                temp = (long) (dim_list.get(i).getMin() + (dim_list.get(i).getRange() + 1) * Math.random());
            else
                temp = (dim_list.get(i).getMin() + (dim_list.get(i).getRange()) * Math.random());
            list.add(temp);
        }
    }

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        int i = 0;
        for (; i < list.size() - 1; i++)
            res.append(list.get(i)).append(",");
        res.append(list.get(i));
        return res.toString();
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        Testcase temp = (Testcase) obj;
        return list.equals(temp.list);
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        super.hashCode();
        return list.hashCode();
    }


    /**
     * Obtain the Minkowski Distance between $testcase$ and $testcase2$
     *
     * @param testcase
     * @param testcase2
     * @param lp        When lp=1: Manhattan Distance
     *                  When lp=2: Euclidean Distance
     * @return
     */
    public static double Distance(Testcase testcase, Testcase testcase2, double lp) {
        if (testcase.size() != testcase2.size()) {
            return Double.MAX_VALUE;
        }
        if (testcase.size() == 1 && testcase2.size() == 1) {
            return Math.abs(testcase.getValue(0) - testcase2.getValue(0));
        }
        double sum = 0.0;
        for (int i = 0; i < testcase.list.size(); i++) {
            sum = sum + (Math.pow(testcase.list.get(i) - testcase2.list.get(i), lp));
        }
        return Math.pow(sum, 1.0 / lp);
    }


    /**
     * Generate the $k$ candidate test cases according to the dimlist
     *
     * @param k       the size of candidate set, default=10
     * @param dimlist
     * @return
     */
    public static ArrayList<Testcase> generateCandates(int k, ArrayList<Dimension> dimlist) {
        ArrayList<Testcase> Candidate = new ArrayList<>();
        for (int j = 0; j < k; j++) {
            Testcase tempcase = new Testcase(dimlist);
            Candidate.add(tempcase);
        }
        return Candidate;
    }

    /**
     * Obtain the Euclidean Distancee between $testcase$ and $testcase2$
     */
    public static double Distance(Testcase testcase, Testcase testcase2) {
        if (testcase.size() != testcase2.size()) {
            return Double.MAX_VALUE;
        }
        if (testcase.size() == 1 && testcase2.size() == 1) {
            return Math.abs(testcase.getValue(0) - testcase2.getValue(0));
        }
        double sum = 0.0;
        for (int i = 0; i < testcase.list.size(); i++) {
            sum = sum + (Math.pow(testcase.list.get(i) - testcase2.list.get(i), 2));
        }
        return Math.sqrt(sum);
    }


    /**
     * Store the test case to the filepath
     *
     * @param tcs      A list of test cases
     * @param filePath target file path
     */
    public static void storeTesecases(ArrayList<Testcase> tcs, String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (Testcase tc : tcs) {
                bw.write(tc.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the file, generate test cases according to beginLine and endline
     *
     * @param tcSet
     * @param filePath
     * @param beginLine
     * @param endLine
     * @throws IOException
     */
    public static void readTcToArray(ArrayList<Testcase> tcSet, String filePath, int beginLine, int endLine)
            throws IOException {
        if (tcSet.size() != beginLine)
            throw new IOException("Error setting for beginLine!");

        InputStream is = new FileInputStream(filePath);
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        for (int i = 0; i < endLine; i++) {
            line = reader.readLine();
            if (line == null) {
                reader.close();
                is.close();
                throw new IOException("Read out of boundary!");
            }

            if (i >= beginLine)
                tcSet.add(new Testcase(line));
        }
        reader.close();
        is.close();
    }


    public double getValue(int i) {
        if (i <= list.size())
            return list.get(i);
        else
            return Double.MAX_VALUE;
    }

    public void addValue(double value) {
        list.add(value);
    }


    public void setValue(int i, double value) {
        list.set(i, value);
    }

    public int size() {
        return list.size();
    }
}
