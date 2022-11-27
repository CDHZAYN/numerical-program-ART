package faultZone;

import java.util.ArrayList;
import java.util.Random;

import model.*;

/**
 * Block failure region.
 */
public class FaultZone_Block extends FaultZone {

    DomainBoundary inputDomain;
    public Testcase faultPoint;
    double fail_totalsize;
    double fail_length;

    public FaultZone_Block() {
        inputDomain = new DomainBoundary();
        faultPoint = new Testcase();
        fail_length = -1;
        fail_totalsize = -1;
    }

    public FaultZone_Block(DomainBoundary dimlist, double failrate) {
        inputDomain = dimlist;
        int n = dimlist.dimensionOfInputDomain();
        fail_rate = failrate;
        double sum = 1.0;

        for (int i = 0; i < n; i++) {
            sum = sum * inputDomain.getDimension(i).getRange();
        }

        fail_length = (double) Math.pow(sum * fail_rate, 1.0 / n);
        faultPoint = new Testcase();

        for (int i = 0; i < n; i++) {
            faultPoint.list.add(inputDomain.getDimension(i).getMin()
                    + (double) ((inputDomain.getDimension(i).getRange() - fail_length) * Math.random()));
        }

    }

    @Override
    public Boolean isCorrect(Testcase testcase) {
        boolean jutice = false;
        for (int i = 0; i < faultPoint.list.size(); i++) {

            if (testcase.list.get(i) < faultPoint.list.get(i)
                    || testcase.list.get(i) > faultPoint.list.get(i) + this.fail_length) {
                jutice = true;
                break;
            }
        }
        return jutice;
    }

}
