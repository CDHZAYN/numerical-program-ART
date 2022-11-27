package faultZone;

import java.util.ArrayList;
import java.util.Arrays;
import model.DomainBoundary;
import model.Testcase;

/**
 * Point failure region.
 */
public class FaultZone_Point_Square extends FaultZone {

	public DomainBoundary inputDomain;
	public int fail_num = 25;
	public ArrayList<Testcase> faultPoints;
	
	public double delta;
 
	public FaultZone_Point_Square() {

	}
 
	public FaultZone_Point_Square(DomainBoundary boundary, double area) {

		fail_rate = area;
		inputDomain = boundary;
		double sum = 1.0;
		int n = boundary.dimensionOfInputDomain();
		for (int i = 0; i < n; i++) {
			sum = sum * (inputDomain.getDimension(i).getRange()); 															//
		}

		delta = Math.pow(sum * fail_rate / fail_num, 1.0 / n); 
		faultPoints = new ArrayList<Testcase>();
		int temp = 0;

		while (temp < fail_num) {
			Testcase faulttemp;
			int overlapCount=0;
			do {
				if(overlapCount>1000000) {//��ֹ����
					overlapCount=0;temp=0;
					faultPoints = new ArrayList<Testcase>();
				}
				faulttemp = new Testcase();
				for (int i = 0; i < n; i++) {
					double value=inputDomain.getDimension(i).getMin()+(double)((inputDomain.getDimension(i).getRange() - delta) * Math.random());
					faulttemp.list.add(value);
				}
				overlapCount++;
			} while (isOverlap(temp, faulttemp, delta));

			faultPoints.add(faulttemp);
			temp++;
		}
	}

	boolean isOverlap(int gNum, Testcase p, double delta) {
		if (gNum == 0)
			return false;
		else {
			for (int i = 0; i < gNum; i++) {
				boolean ftemp = true;
				for (int j = 0; j < p.list.size(); j++) {
					if (!(Math.abs(p.list.get(j) - faultPoints.get(i).list.get(j)) < delta)) {
						ftemp = false;
						break;
					}
				}
				if (ftemp) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public Boolean isCorrect(Testcase testcase) {
		for (int i = 0; i < faultPoints.size(); i++) {
			boolean ftemp = true;
			Testcase temp = faultPoints.get(i);
			for (int j = 0; j < temp.size(); j++) {
				if (!(testcase.getValue(j) >= temp.getValue(j)
						&& testcase.list.get(j)  <= temp.getValue(j) + this.delta)) {
					ftemp = false;
					break;
				}
			}
			if (ftemp) {
				return false;
			}
		}
		return true;
	}
}

