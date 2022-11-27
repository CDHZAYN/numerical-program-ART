package model;


public class Dimension {
	/**
	 * The dimension of inputdomain.
	 */
	double min;
	double max;
	double range;   // the range in each dimension.
	public Dimension(double min,double max) {
		this.min=min;
		this.max=max;
		range=max-min;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public double getRange() {
		return range;
	}

	@Override
	public String toString() {
		return "min="+min+",max="+max;
	}
}
