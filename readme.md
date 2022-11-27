# ARTEmpirical

## src/faultZone
==Simulation settings, no modification required==
strore the various failure pattern including:
```
FaultZone (abstract class): check whehter the test case locat in this failure region. 
FaultZone_Block: the block failure pattern
FaultZone_Point: the point failure pattern
FautZone_Strip: tht Strip failure pattern
```
## src/fscs
Only a example of FSCS-ART algorithm. Refer to this example

## src/model
```
AbstractART: abstract method, all ART algorithms must must inherit from this class.
Dimension: determine the dimension of input domain.
DomainBoundary: determine the boundary of input domain.
Parameters: This class stores some of the parameters necessary for the ART algorithm.
TestCase: the generated test cases by various ART algorithms.
ThreadWithCallback: temporarily abandoned.
```

## src/simulation
==Some necessary parameters are hard-coded into the code and subsequently need to be decoupled into the ```src/model/Parameters```. At this stage, the parameters can be modified directly in the code to test the correctness of the algorithm.==
```
StoreResults: temporarily abandoned.
TestEffectiveness: This class mainly tests the effectiveness of the ART algorithm under simulation.
TestEfficiency: This class mainly tests the efficiency of the ART algorithm under simulation.
```