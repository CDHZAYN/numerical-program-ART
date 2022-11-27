
package faultZone;

import java.util.Random;

import model.DomainBoundary;
import model.Testcase;

/**
 * Strip failure region.
 */

public class FaultZone_Strip extends FaultZone {

	public DomainBoundary inputDomain;
	public double edge;
	public double aboveLineDelta;
	public double belowLineDelta;
	public double ratio;

	public FaultZone_Strip() {

	}

	/**
	 *
	 * @param boundary  inputdomain Min=-5000 Max=5000
	 * @param area failure rate
	 * @param rate default=0.9
	 */
	public FaultZone_Strip(DomainBoundary boundary, double area, double rate) {
		inputDomain = boundary;
		fail_rate = area;
		edge = inputDomain.getDimension(0).getRange();

		Random random = new Random();
		int lineLocation = random.nextInt(3);

		double p1x, p1y, p2x, p2y, p3x, p3y, p4x, p4y;

		if (lineLocation == 0) {
			while (true) {
				p1x = -5000;
				p2x = -5000;
				p2y = -5000 + (10000 * rate * Math.random());
				p3y = 5000;
				p4x = (-5000 + (10000 * (1 - rate))) + (10000 * rate * Math.random());
				p4y = 5000;
				double bigTriangleArea = (5000 - p2y) * (p4x + 5000) / 2;
				ratio = (p4y - p2y) / (p4x - p2x);
				double temp = 2 * (bigTriangleArea - 10000 * 10000 * area) / ratio;
				p3x = Math.sqrt(temp) - 5000;
				p1y = 5000 - ratio * (p3x + 5000);
				if ((p3x >= (-5000 + (10000 * (1 - rate)))) && (p1y <= (-5000 + 10000 * rate))) {
					break;
				}
			}
		} else if (lineLocation == 1) {
			while (true) {
				p1x = -5000;
				p2x = -5000;
				p2y = -5000 + (10000 * Math.random());
				p3x = 5000;
				p4x = 5000;
				p4y = (-5000 + (10000 * Math.random()));
				p1y = p2y + 10000 * fail_rate;
				p3y = p4y + 10000 * fail_rate;
				ratio = (p4y - p2y) / (p4x - p2x);
				if (p1y <= 5000 && p3y <= 5000) {
					break;
				}
			}
		} else {
			while (true) {
				p1x = -5000;
				p1y = (-5000 + (10000 * (1 - rate))) + (10000 * rate * Math.random());
				p2x = -5000;
				p3x = (-5000 + (10000 * (1 - rate))) + (10000 * rate * Math.random());
				p3y = -5000;
				p4y = -5000;
				ratio = (p3y - p1y) / (p3x - p1x);
				double bigTriangleArea = (p1y + 5000) * (p3x + 5000) / 2;
				double temp = 2 * (10000 * 10000 * area - bigTriangleArea) / ratio;
				p4x = Math.sqrt(temp) - 5000;
				p2y = -ratio * (p4x + 5000) - 5000;
				if ((p4x >= (-5000 + (10000 * (1 - rate)))) && (p2y >= (-5000 + (10000 * (1 - rate))))) {
					break;
				}
			}
		}
		aboveLineDelta = p1y - ratio * p1x;
		belowLineDelta = p4y - ratio * p4x;
	}


	@Override
	public Boolean isCorrect(Testcase testcase) {
		if (testcase.getValue(1) - ratio * testcase.getValue(0) >= belowLineDelta
				&& testcase.getValue(1) - ratio * testcase.getValue(0) <= aboveLineDelta) {
			return false;
		} else
			return true;
	}
}
