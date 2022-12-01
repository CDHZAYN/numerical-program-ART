package util;

import java.util.ArrayList;

public  class Node {
    public int spilt; // 分割维度的序号
    public Node left; // 左子树
    public Node right; // 右子树
    public Node parent; // 父节点
    public Testcase tc; // 该节点中的一个测试点
    public double[][] boundary; // 该节点所处空间的边界区域
    public int deep;

    public Node(){ super();}
}