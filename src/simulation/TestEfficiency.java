package simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fscs.FSCS_art;
import model.AbstractART;
import model.DomainBoundary;
import model.Parameters;

public class TestEfficiency {

	final static int times = 1003;

	public static void main(String[] args) throws IOException {
		int[] dimensionList = {3};

		for (int dimension : dimensionList) {
			efficiency(dimension, FSCS_art.class);
		}
	}

	public static void efficiency(int dim, Class<? extends AbstractART> method) {
		ArrayList<Integer> nums = new ArrayList<>();
		nums.add(200);

		DomainBoundary inputBoundary=new DomainBoundary(dim,-5000,5000);

		for (int num : nums) {
			String s1 = "./output2/pn_" + num +"d_"+dim+ ".txt";

			try {
				testART(dim, s1, inputBoundary, num,Parameters.lp, method);
			} catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void testART(int dim, String file, DomainBoundary inputBoundary, int pointNum, double lp, Class<? extends AbstractART> method) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		FSCS_art fscs;
		double sum = 0;
		ArrayList<Double> result=new ArrayList<Double>();
        Constructor constructor = method.getConstructor(DomainBoundary.class, Double.class);
		for (int i = 0; i < times; i++) {
			long n1 = System.currentTimeMillis();
            AbstractART fscs_block = (AbstractART) constructor.newInstance(inputBoundary, Parameters.lp);
			fscs = new FSCS_art(inputBoundary,lp);
			fscs.testEfficiency(pointNum);
			long n2 = System.currentTimeMillis();

			if (i != 0 && i != 1 && i != 2) {
				double temp=n2 - n1;
				sum = sum + temp;
				result.add(temp);
				System.out.print(i+"_"+temp+"\t");
			}
		}
		double num = times - 3;
		System.out.println("\r\n ART d="+dim+"\tpointNum="+pointNum+"\t"+sum / num  + "\t");

	}

}
