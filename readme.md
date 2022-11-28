# ARTEmpirical

## src/art
向里面填不同的算法，需要实现AbstractART抽象类的以下方法：
* bestCandidate：描述如何找到下一个测试用例。
* testEfficiency：使用bestCandidate生成测试用例的过程。

## src/dt
**注意：**
把群里的第二个zip下下来，里面有origin和mutant文件夹，把他们复制进src/dt里，这两个文件夹太大了，就不传github了

## src/faultZone
包括三种faultzone模型：
* 一个正方体；
* 一个圆柱（至少差不多是个圆柱）； 
* 多个小正方体

## src/entry
包括两种本次大作业要求的度量方法，均继承自AbstractEntry抽象类，使用这里面的方法完成整体测试：
* FaultZoneEntry：使用fault zone模型完成模拟测试。
* RealCodeEntry：使用实际的数值程序完成实际测试。

## src/util
包含多种本项目需要的实用类：
* Dimension：表示单个维度的范围，一个维度对应一个输入参数。
* DomainBoundary：表示输入空间，由多个Dimension组成，即多个输入参数。
* Parameters：本项目统一的参数。
* StoreResults：保存测试用例于指定的文件。
* TestCase：根据指定DomainBoundary生成的测试用例，还包含一些测试用例需要的方法。
* TreadWithCallback：没用。