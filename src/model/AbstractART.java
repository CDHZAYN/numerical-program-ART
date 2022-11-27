package model;

import faultZone.FaultZone;

import java.util.ArrayList;

public abstract class AbstractART {
    public ArrayList<Testcase> total = new ArrayList<>();
    public ArrayList<Testcase> Candidate = new ArrayList<>();
    int count=0;


    public int run(FaultZone fz) {
        Testcase testcase=null;
        do{
            testcase = Best_candidate();
            count++;
            total.add(testcase);
        }while (fz.isCorrect(testcase));
        return count;
    }

    public abstract Testcase Best_candidate();

    public abstract void testEfficiency(int pointNum);


}



