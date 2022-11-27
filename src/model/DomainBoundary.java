package model;

import java.util.ArrayList;

public class DomainBoundary {
	/**
	 * Construct the inputdomain
	 */
	ArrayList<Dimension> BoundaryData;

	public DomainBoundary() {
		BoundaryData = new ArrayList<Dimension>();
	}

	/**
	 * The each dimension of inputdomain is (min, max)
	 */
	public DomainBoundary(int n, double min, double max) {
		BoundaryData = new ArrayList<Dimension>();
		for (int i = 0; i < n; i++) {
			BoundaryData.add(new Dimension(min, max));
		}
	}

	public DomainBoundary(ArrayList<Dimension> domainboundary) {
		BoundaryData = domainboundary;
	}

	public DomainBoundary(DomainBoundary domainboundary) {
		this.BoundaryData = new ArrayList<Dimension>();
		double min, max;
		for (int i = 0; i < domainboundary.dimensionOfInputDomain(); i++) {
			min = domainboundary.getDimension(i).getMin();
			max = domainboundary.getDimension(i).getMax();
			this.BoundaryData.add(new Dimension(min, max));
		}
	}

	public Dimension getDimension(int i) {
		return this.BoundaryData.get(i);
	}

	public void setDimension(int i, Dimension dim) {
		this.BoundaryData.set(i, dim);
	}

	public int dimensionOfInputDomain() {
		return this.BoundaryData.size();
	}

	public double sizeOfInputDomain() {
		double size = 1;
		for (Dimension dim : BoundaryData)
			size *= dim.range;
		return size;
	}

	/**
	 *
	 * @param tc: generated test cases
	 * @return whether the $tc$ is inside the inputdomain.
	 */
	public boolean isInside(Testcase tc) {
		if (tc.size() != BoundaryData.size())
			throw new IllegalArgumentException(
					"The dimension of test case is not equal to the dimension of input domain!");

		for (int i = 0; i < BoundaryData.size(); i++) {
			if (tc.getValue(i) < BoundaryData.get(i).getMin() || BoundaryData.get(i).getMax() < tc.getValue(i))
				return false;
		}
		return true;
	}


	public void addDimension(Dimension newDim) {
		this.BoundaryData.add(newDim);
	}

	public ArrayList<Dimension> getList() {
		return this.BoundaryData;
	}

	public void setList(ArrayList<Dimension> dimlist) {
		this.BoundaryData = dimlist;
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < BoundaryData.size(); i++) {
			Dimension dimBoundary = BoundaryData.get(i);
			res.append("dimension ").append(i).append(": (");
			res.append(dimBoundary.getMin()).append(", ").append(dimBoundary.getMax());
			res.append(") range=").append(dimBoundary.getRange()).append("\r\n");
		}
		return res.toString();
	}
}
