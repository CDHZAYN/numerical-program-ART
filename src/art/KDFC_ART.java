package art;

import java.util.*;

import faultZone.FaultZone;
import faultZone.FaultZone_Point_Square;
import util.*;


/**
 * KDFC（2019）
 * 论文：KDFC-ART: a KD-tree approach to enhancing Fixed-size-Candidate-set Adaptive Random Testing
 * 大致方法：
 * 1. 随机生成一个测试用例，作为KD树的根
 * 2. 从测试用例集中找到距离最远的候选点，并将其插入到已执行测试集KD树中
 */

public class KDFC_ART extends AbstractART{
    public Node root; // 根节点
    public int size = 0; // 树中节点个数
    public int candidateNum = 10; // 候选用例个数
    public DomainBoundary inputDomain; // 输入域范围

    public KDFC_ART() {
    }

    public KDFC_ART(DomainBoundary inputDomain){
        this.inputDomain = inputDomain;
    }
    public KDFC_ART(ArrayList <Dimension> bound) {
        super();
        root = new Node();
        inputBoundary =new DomainBoundary(bound);
    }

    public Stack<Node> getTreePath(Testcase tc) { // 获得p点在该树中的搜索路径

        Stack<Node> path = new Stack<>();
        Node pathNode = this.root;

        while (true) {
            path.push(pathNode);
            if (pathNode.tc.list.get(pathNode.spilt) >tc.list.get(pathNode.spilt)){ // 点p在pathNode左边
                if (pathNode.left == null) {
                    break;
                }
                pathNode = pathNode.left;
            } else { // 点p在pathNode右边
                if (pathNode.right == null) {
                    break;
                }
                pathNode = pathNode.right;
            }
        }
        return path;
    }

    public int judgeDirection(Testcase tc, Node node) { // 判断点p在node节点的左边还是右边 0为左边 1为右边
        if (tc.list.get(node.spilt) < node.tc.list.get(node.spilt)) {
            return 0;
        } else
            return 1;
    }

    public double getMinDis(Testcase tc) { // 求出点p距离树中节点的最短距离

        Stack<Node> path = this.getTreePath(tc);
        Node pathNode = null;
        double distance = Double.MAX_VALUE;

        while ((!path.isEmpty())) {
            pathNode = path.pop(); // 从主路径中pop出pathNode节点
            if (this.isCrossSpiltLine(tc, distance, pathNode)) { // 当前distance和PathNode所在的边界线相交时，需进入另一边查询
                double d = Testcase.Distance(tc, pathNode.tc);
                if (distance > d) {
                    distance = d;
                }

                int direction = this.judgeDirection(tc, pathNode);// 判断点p在pathNode的左边还是右边
                Node tempNode = null;

                if (direction == 0) {
                    if (pathNode.right != null) {
                        tempNode = pathNode.right;
                    }
                } else {
                    if (pathNode.left != null) {
                        tempNode = pathNode.left;
                    }
                }

                if (tempNode != null) {
                    Queue<Node> queue = new LinkedList<>();
                    queue.offer(tempNode);
                    while (!queue.isEmpty()) { // 对子树进行广度优先遍历
                        tempNode = queue.poll();
                        direction = this.judgeDirection(tc, tempNode);
                        if (this.isCrossSpiltLine(tc, distance, tempNode)) { // 如果p节点和子树节点的边界线相交，则将子树节点的另一边push进队列
                            d = Testcase.Distance(tc, tempNode.tc);
                            if (distance > d) {
                                distance = d;
                            }

                            if (direction == 1) {
                                if (tempNode.left != null) {
                                    queue.offer(tempNode.left);
                                }
                            } else {
                                if (tempNode.right != null) {
                                    queue.offer(tempNode.right);
                                }
                            }
                        }

                        if (direction == 0) { // 在左边，首先将左边节点push进队列
                            if (tempNode.left != null) {
                                queue.offer(tempNode.left);
                            }
                        } else { // 在右边，首先将右边节点push进队列
                            if (tempNode.right != null) {
                                queue.offer(tempNode.right);
                            }
                        }

                    }
                }
            }

        }
        return distance;
    }

    public Boolean isCrossSpiltLine(Testcase tc, double distance, Node node) { // 判断以节点p为中心，distance为半径的圆是否与node所在的边界线相交

        if (Math.abs(node.tc.list.get(node.spilt) - tc.list.get(node.spilt)) >= distance) { // 分界线
            return false;
        }
        return true;

    }

    public void insertPointByTurn(Testcase tc) { // 轮流各个维度向tree中插入新的节点p
        if (root.tc == null) { // 如果root节点中的p点为空
            root.tc = tc;
            root.spilt = 0; // 第一层设置分裂为x
            root.deep = 1;
        } else {
            Node ntemp = root;
            Node n = new Node();
            while (true) {
                if (ntemp.tc.list.get(ntemp.spilt) > tc.list.get(ntemp.spilt)) {
                    if (ntemp.left == null) { // ntemp的左边为空，则将n设置为左边子树,并退出循环
                        ntemp.left = n;
                        break;
                    }
                    ntemp = ntemp.left;
                } else {
                    if (ntemp.right == null) { // ntemp的右边为空，则将n设置为右边子树，并退出循环
                        ntemp.right = n;
                        break;
                    }
                    ntemp = ntemp.right;
                }

            }
            n.tc = tc;
            n.deep = ntemp.deep + 1;
            if (ntemp.spilt == (tc.list.size() - 1)) {
                n.spilt = 0;
            } else
                n.spilt = ntemp.spilt + 1;
        }
        size++;
    }


    public static void main(String args[]){
        int times = 3000;//一次执行，该算法就重复3000次
        long sums = 0;// 初始化使用的测试用例数
        int temp = 0;// 初始化测试用例落在失效域的使用的测试用例的个数

        ArrayList<Integer> result = new ArrayList<>();
        //统一p=2，表示计算输入间距离按正常方法计算（各维度距离平方和开平方）
        double p = Parameters.lp;
        double failrate = 0.005;
        int dimension = 2;
        //二维输入（即一次输入两个参数），两个数值参数的上限（5000）、下限（5000）相同
        DomainBoundary bd = new DomainBoundary(dimension, -5000, 5000);

        for (int i = 1; i <= times; i++) {
            //指定使用这种fault zone
            FaultZone fz = new FaultZone_Point_Square(bd, failrate);
            KDFC_ART kdfc_block = new KDFC_ART(bd);
            //小run一下
            temp = kdfc_block.runWithFaultZone(fz);
            result.add(temp);
            System.out.println("第" + i + "次试验F_Measure：" + temp);
            sums += temp;
        }

        System.out.println("KDFC_block当前参数：dimension = " + dimension + "   lp = " + p + "   failure-rate = " + failrate); //输出当前参数信息
        System.out.println("Fm: " + sums / (double) times + "  且最后的Fart/Frt: "
                + sums / (double) times * failrate);// 平均每次使用的测试用例数

    }

    @Override
    public Testcase bestCandidate() {
        //总之先生成10个新的候选输入
        this.candidate.clear();
        this.candidate = Testcase.generateCandates(10, inputDomain.getList());

        Testcase p = null;
        double mindist, maxmin = 0;
        int index = -1;
        //如果之前没有进行过候选输入选择，则随机生成一个输入当作找到了
        if (total.size() == 0) {
            return candidate.get(new Random().nextInt(candidate.size()));
        }
        for (int i = 0; i < this.candidate.size(); i++) {
            mindist = Double.MAX_VALUE;
            //对候选输入，挨个测试其与已使用过的输入的距离，记录该过程中的最小距离
            for (int j = 0; j < this.total.size(); j++) {
                double dist = Testcase.Distance(this.candidate.get(i), this.total.get(j));
                if (dist < mindist) {
                    mindist = dist;
                }
            }
            //找到最小距离最大的一个候选输入，然后返回
            if (maxmin < mindist) {
                maxmin = mindist;
                index = i;
            }
        }
        return this.candidate.get(index);
    }


    @Override
    public void testEfficiency(int pointNum) {
        Testcase p = new Testcase(inputDomain); // 随机产生一个用例
        this.insertPointByTurn(p);
        Testcase finalCase;
        ArrayList<Testcase> canD;
        for (int i = 1; i < pointNum; i++) {
            canD = new ArrayList<>(); // 测试用例候选集
            for (int j = 0; j < candidateNum; j++) {
                canD.add(Testcase.generateCandate(inputDomain.getList()));
            }
            finalCase = canD.get(0);
            double distance = this.getMinDis(finalCase);
            for (int c = 1; c < candidateNum; c++) {
                double d = this.getMinDis(canD.get(c)); // 获得最小距离
                if (distance < d) { // 获得最小距离最大的那个候选点
                    distance = d;
                    finalCase = canD.get(c);
                }
            }
            this.insertPointByTurn(finalCase);
        }
    }
}