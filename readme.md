# ARTEmpirical

## 补充顺序（也可以看idea的TODO工具）
1. 确定算法名称，在src/art文件夹中新建类（如NewART），填写注释；
2. 补充main、bestCandidate、testEfficiency三个方法（如NewART）和以DomainBoundary作为参数的该类的初始化方法（参见NewART）。
> 事实上testEfficiency可以照抄，TPP_ART里的更简单一点。
3. 如果要用entry，把AbstractEntry.java里的testingART字段改成新加的算法名称（如NewART）。

## src/art
向里面填不同的算法，需要实现AbstractART抽象类的以下方法：
* bestCandidate：描述如何找到下一个测试用例。
* testEfficiency：使用bestCandidate生成测试用例的过程。

## src/realCodes
真实的数值程序，original里是源程序，mutant里是错误的变体程序。
<br>
**注意**：如果要测试realCodeEntry能用，把original和mutant（在q群最后一个zip里）复制进来之后需要：
1. 把他俩放到src/dt文件夹里；
2. 把realCodes里的两个类复制进dt里，删掉realCodes文件夹；
3. 用idea把dt这个文件夹（包）改名成realCodes，用idea是为了保证两个文件夹里的程序的包名耶跟着一起改。

## src/faultZone
包含三种faultzone模型：
* FaultZone_Block：一个正方体；
* FaultZone_Strip：一个圆柱（至少差不多是个圆柱）；
* FaultZone_Point_Square：多个小正方体。

以及一个辅助记录输入空间划分的类：
* PartitionTree，树状划分记录，具体看注释，可能有bug，待修。

## src/entry
包括两种本次大作业要求的度量方法，均继承自AbstractEntry抽象类，**使用以下两个类完成生成测试结果**：
* FaultZoneEntry：使用fault zone模型完成模拟测试。
* RealCodeEntry：使用实际的数值程序完成实际测试。

## src/util
包含多种本项目需要的实用类：
* Dimension：表示单个维度的范围，一个维度对应一个输入参数。
* DomainBoundary：表示输入空间，由多个Dimension组成，即多个输入参数。
* Parameters：本项目统一的参数。
* StoreResults：保存测试用例于指定的文件。
* TestCase：根据指定DomainBoundary生成的测试用例，还包含一些测试用例需要的方法。