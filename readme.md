# 项目结构说明

## 文件结构树形图

```
├─out
│  ├─faultZone
│  └─realCode
└─src
    ├─art
    ├─entry
    ├─faultZone
    ├─realCodes
    │  ├─mutant
    │  └─original
    └─util
```

## 文件夹内容

以下内容同**实验报告.pdf**。

## out/faultZone

**存放错误空间模型的测试结果文件。**

## out/realCode

**存放真实数值程序的测试结果文件。**

## src/art

**存放不同的ART算法**，实现的算法均继承**AbstractART抽象类**的以下方法：

- main：单独执行该算法的入口方法。

- bestCandidate：描述如何找到下一个测试用例。

- testEfficiency：使用bestCandidate方法生成测试用例。

Abstract抽象类作为算法框架，提供了与测试框架交互的接口，描述了算法如何在测试框架的指导下运行。其中的重要方法有：

- runWithFaultZone：描述算法如何使用错误空间模型模拟的程序输入空间运行。

- runWithNumericProgram：描述算法如何使用真实数值程序的输入空间运行。

## src/entry

**存放运行该测试框架的两个“入口”**，且这两个入口均继承自**AbstractEntry抽象类**：

- FaultZoneEntry：使用错误空间的测试入口。

- RealCodeEntry：使用真实数值程序的入口。

AbstractEntry实现了一些两种入口均需要的方法，包括：

- testEfficiency：测试算法效率的通用方法。

- storeResult：使用util包中的StoreResults类将测试结果输出至指定路径的文件中。

## src/faultZone

**存放三种错误空间模型**，用于模拟输入空间：

- FaultZone_Block：错误空间被抽象为一个正方体（三维输入空间中）。

- FaultZone_Strip：错误空间被抽象为一个类圆柱体（三维输入空间中）。

- FaultZone_Point_Square：错误空间被抽象为多个小正方体（三维输入空间中）。

## src/realCodes

存放实际的数值程序，我们使用其中的部分进行算法评估，包含两个文件夹：

- original：数值程序文件，包括不同参数、不同用途的数值程序。

- mutant：包含original文件夹中数值程序的变异程序，按原程序名称储存于对应的文件夹中。

我们还实现了两个辅助类，用于更简便地运行数值程序：

- RealCodesDriver：用于驱动数值程序运行。

- RealCodesRunner：**将数值程序在运行时转化为独立线程**，方便控制单次运行时间，发现无限循环等错误。

## src/util

**包含多种本项目需要的实用类**：

- Dimension：表示单个维度的范围，一个维度对应一个输入参数。

- DomainBoundary：表示输入空间，由多个Dimension组成，即多个输入参数。

- Parameters：存放本项目统一的参数，比如单次运行次数、文件保存路径等。

- StoreResults：保存测试用例于指定的文件。

- TestCase：根据指定DomainBoundary生成的测试用例，还包含一些生成与比较测试用例需要的方法。

- Node：作用于部分算法的测试用例KD树节点类。

- PartitionTree：我们发现有相当一部分的ART算法使用了输入空间的划分技术，因此我们自行设计了**树状记录输入空间划分的PartitionTree类**，用于记录划分区域的范围。该类在实际使用中构成了一棵多叉树，树的父节点的范围是其子节点范围的并集，方便递归地查找与记录当前测试用例所在的划分。