package faultZone;

import model.*;

/**
 * Simulation experiments-abstract class
 */
public abstract class FaultZone {
	public double fail_rate;
	public abstract Boolean isCorrect(Testcase testcase);
	
}
