package util;

import java.util.ArrayList;
import java.util.List;

public class PartitionTree {
    // 基本思想是生成一棵树，单个树节点（如A）包含该输入空间划分的范围，
    // A的子节点的范围是基于A的，即从根节点找到指定节点的过程中逐渐（在仅垂直、水平方向上）限定输入空间的范围。=

    // 对于根节点：对应的DomainBoundary；
    // 对于其他节点：原DomainBoundary中的指定数量的随即维度水平或垂直切割。
    private DomainBoundary domainBoundary;

    // 直接子节点列表
    private List<PartitionTree> directChildren = new ArrayList<>();

    // 该划分下的所有测试用例
    private List<Testcase> testcaseList = new ArrayList<>();

    private String color;

    // 用于创建根划分树节点
    public PartitionTree(DomainBoundary domainBoundary) {
        this.domainBoundary = domainBoundary;
    }

    public void partition(int partitionDimSum, Testcase partitionPoint) {
        if (partitionDimSum <= 1) return;

        // 确定进行划分的维度是哪几个，用isPartitionDim记录
        List<Dimension> dimensions = domainBoundary.getList();
        List<Boolean> isPartitionDim = new ArrayList<>();
        for (int i = 0; i < domainBoundary.dimensionOfInputDomain(); ++i)
            isPartitionDim.add(false);
        int partitionDim = 0;
        for (int i = 0; i < partitionDimSum; ++i) {
            int tmp;
            do {
                tmp = (int) (Math.random() * dimensions.size());
            } while (tmp < dimensions.size() && isPartitionDim.get(tmp));
            isPartitionDim.set(tmp, true);
        }

        // 如果有指定划分点，则以划分点为中心划分；
        // 如果没有指定的划分点，则默认中心划分
        List<DomainBoundary> partitionedDomainBoundaries = getPartitionedDomainBoundaries(dimensions, isPartitionDim,
                partitionPoint);
        for (DomainBoundary dmbdry : partitionedDomainBoundaries)
            directChildren.add(new PartitionTree(dmbdry));

        //向子节点同步已测试用例
        for (PartitionTree child : directChildren)
            child.addTestcases(testcaseList);
    }

    //将分割过的划分放在一个列表中，依次在每个需要划分的维度，把列表中的所有划分再次分割。
    private List<DomainBoundary> getPartitionedDomainBoundaries(List<Dimension> dimensions, List<Boolean> isPartitionDim,
                                                                Testcase partitionPoint) {
        List<List<Dimension>> dimensionsList = new ArrayList<>();
        dimensionsList.add(dimensions);
        for (int i = 0; i < isPartitionDim.size(); ++i) {
            if (isPartitionDim.get(i)) {
                List<List<Dimension>> newDimensionsList = new ArrayList<>();
                for (List<Dimension> dmss : dimensionsList) {
                    double pttPoint = dmss.get(i).getMin() + dmss.get(i).getRange() / 2;
                    if (partitionPoint != null)
                        pttPoint = partitionPoint.getValue(i);

                    List<Dimension> dmss1 = new ArrayList<>(dmss);
                    Dimension dms1 = new Dimension(pttPoint, dmss1.get(i).getMax());
                    dmss1.set(i, dms1);
                    newDimensionsList.add(dmss1);

                    List<Dimension> dmss2 = new ArrayList<>(dmss);
                    Dimension dms2 = new Dimension(dmss2.get(i).getMin(), pttPoint);
                    dmss2.set(i, dms2);
                    newDimensionsList.add(dmss2);
                }
                dimensionsList = newDimensionsList;
            }
        }
        List<DomainBoundary> domainBoundaryList = new ArrayList<>();
        for (List<Dimension> dmss : dimensionsList)
            domainBoundaryList.add(new DomainBoundary((ArrayList<Dimension>) dmss));
        return domainBoundaryList;
    }

    //找到包含指定测试用例的“最细”划分。
    public PartitionTree getPartitionTreeNode(Testcase testcase) {
        boolean isContain = false;
        if (testcaseList.isEmpty()) return null;
        for (Testcase tc : testcaseList) {
            if (tc.equals(testcase)) {
                isContain = true;
                break;
            }
        }
        if (!isContain) return null;
        if (directChildren.isEmpty())
            return this;
        for (PartitionTree child : directChildren) {
            PartitionTree temp = child.getPartitionTreeNode(testcase);
            if (temp != null)
                return temp;
        }
        return null;
    }

    //向包含指定测试用例的所有划分树节点中的测试用例列表添加记录，并返回包含该测试用例“最细”的划分。
    //注意：务必从根节点调用该函数。
    public PartitionTree addTestcase(Testcase testcase) {
        PartitionTree result = null;
        if (domainBoundary.isInside(testcase))
            testcaseList.add(testcase);
        else
            return null;
        if (directChildren.isEmpty())
            return this;
        for (PartitionTree child : directChildren) {
            PartitionTree temp = child.addTestcase(testcase);
            if (temp != null)
                result = temp;
        }
        return result;
    }

    private void addTestcases(List<Testcase> testcaseList) {
        for (Testcase testcase : testcaseList)
            addTestcase(testcase);
    }

    //获取所有的“最细”划分
    public List<PartitionTree> getLeaveTreeNodes() {
        List<PartitionTree> result = new ArrayList<>();
        result.add(this);

        //标识result列表中是否还有节点新增
        boolean isInProcess = true;
        while (isInProcess) {
            isInProcess = false;
            List<PartitionTree> newNodes = new ArrayList<>();
            for (PartitionTree node : result) {
                if (!node.directChildren.isEmpty()) {
                    isInProcess = true;
                    for (PartitionTree child : node.directChildren)
                        newNodes.add(child);
                } else {
                    newNodes.add(node);
                }
            }
            result = newNodes;
        }
        return result;
    }

    public PartitionTree getOneLeaf(){
        if(this.directChildren.isEmpty())
            return this;
        return this.directChildren.get(0).getOneLeaf();
    }

    public double getSize() {
        return domainBoundary.sizeOfInputDomain();
    }

    public DomainBoundary getDomainBoundary() {
        return domainBoundary;
    }

    public ArrayList<Testcase> gettestcaseList() {
        return (ArrayList<Testcase>) testcaseList;
    }

    public int getTestcaseNum() {
        return testcaseList.size();
    }

    public void setcolor(String Color){
        this.color = Color;
    }

    public String getcolor(){
        return this.color;
    }


}
