package cn.edu.nju.czh.algorithm;
import cn.edu.nju.czh.preprocessing.ItemSet;
import cn.edu.nju.czh.preprocessing.Pattern;
import cn.edu.nju.czh.preprocessing.Transaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;


class TreeNode {
    private String name;
    private int count;
    private TreeNode parent;
    private List<TreeNode> children ;
    private TreeNode next;
    private TreeNode tail;

    @Override
    public String toString() {
        return name;
    }

     public TreeNode(String name) {
        this.name = name;
     }

     public TreeNode() {}

     public String getName() {
        return name;
     }

     public void setName(String name) {
        this.name = name;
     }

     public int getCount() {
        return count;
     }

     public void setCount(int count) {
        this.count = count;
     }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public TreeNode getNext() {
        return next;
    }

    public void setNext(TreeNode next) {
        this.next = next;
    }

    public TreeNode getTail() {
        return tail;
    }

    public void setTail(TreeNode tail) {
        this.tail = tail;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public void addChild(TreeNode node) {
        if (children == null) {
            children = new ArrayList<TreeNode>();
            children.add(node);
        } else {
            children.add(node);
        }
    }

    public void countIncrement(int x) {
        this.count += x;
    }
    public TreeNode findChild(String name) {
        List<TreeNode> children = getChildren();
        if (children != null) {
            for (TreeNode child : children) {
                if (child.getName().equals(name)) {
                    return child;
                }
            }
        }
        return null;
    }
}

public class FpGrowth implements Method{
    private List<List<String>> transactions = new LinkedList<>();
    private ArrayList<ArrayList<Pattern>> allFrequentPatterns = new ArrayList<>();
    private ArrayList<TreeNode> header = new ArrayList<>();
    /**存储每个频繁项及其对应的计数**/
    private Map<List<String>, Integer> frequentMap = new HashMap<List<String>, Integer>();
    private int minSup = 30;
    private String logPath;
    private int totalSize;

    public void setMinSup(int minSup) {
        this.minSup = minSup;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        for(Transaction transaction : transactions) {
            this.transactions.add(transaction.getItems());
        }
    }

    private void init() {
        allFrequentPatterns.clear();
        //先将事务变成若干1频繁项集(计算频数)
        HashMap<String,Integer> hashMap = new HashMap<>();
        for(List<String> transaction:transactions) {
            for(String str:transaction) {
                if(hashMap.get(str)!=null) {
                    hashMap.put(str, hashMap.get(str) + 1);
                }
                else {
                    hashMap.put(str, 1);
                }
            }
        }

        for(String str:hashMap.keySet()) {
            int num = hashMap.get(str);
            if(num >= minSup) {
                TreeNode treeNode = new TreeNode(str);
                treeNode.setCount(num);
                header.add(treeNode);
            }
        }
        //按照项的出现次数从大到小排序

        header.sort(new Comparator<TreeNode>() {
            @Override
            public int compare(TreeNode o1, TreeNode o2) {
                int num1 = o1.getCount();
                int num2 = o2.getCount();
                if(num1 < num2) {
                    return 1;
                }
                else if (num1 == num2){
                    return 0;
                }
                else {
                    return -1;
                }
            }
        });

        //for(TreeNode treeNode : header) {
        //    System.out.println(treeNode.getName()+" "+treeNode.getCount());
        //}
        //System.out.println(patternsList);


    }

    /**
     * 生成一个序列的各种子序列。（序列是有顺序的）
     *
     */
    private void combine(LinkedList<TreeNode> residualPath, List<List<TreeNode>> results) {
        if (residualPath.size() > 0) {
            //如果residualPath太长，则会有太多的组合，内存会被耗尽的
            TreeNode head = residualPath.poll();
            List<List<TreeNode>> newResults = new ArrayList<List<TreeNode>>();
            for (List<TreeNode> list : results) {
                List<TreeNode> listCopy = new ArrayList<TreeNode>(list);
                newResults.add(listCopy);
            }

            for (List<TreeNode> newPath : newResults) {
                newPath.add(head);
            }
            results.addAll(newResults);
            List<TreeNode> list = new ArrayList<TreeNode>();
            list.add(head);
            results.add(list);
            combine(residualPath, results);
        }
    }

    private boolean isSingleBranch(TreeNode root) {
        boolean rect = true;
        while (root.getChildren() != null) {
            if (root.getChildren().size() > 1) {
                rect = false;
                break;
            }
            root = root.getChildren().get(0);
        }
        return rect;
    }

    private Map<String, Integer> getFrequency(List<List<String>> transRecords) {
        Map<String, Integer> rect = new HashMap<String, Integer>();
        for (List<String> record : transRecords) {
            for (String item : record) {
                Integer cnt = rect.get(item);
                if (cnt == null) {
                    cnt = new Integer(0);
                }
                rect.put(item, ++cnt);
            }
        }
        return rect;
    }

    private void buildFPTree(List<List<String>> transRecords) {
        totalSize = transRecords.size();
        //计算每项的频数
        final Map<String, Integer> freqMap = getFrequency(transRecords);
        for (List<String> transRecord : transRecords) {
            Collections.sort(transRecord, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return freqMap.get(o2) - freqMap.get(o1);
                }
            });
        }
        growth(transRecords, null);
    }

    private void growth(List<List<String>> cpb, LinkedList<String> postModel) {
        //        System.out.println("CPB is");
        //        for (List<String> records : cpb) {
        //            System.out.println(records);
        //        }
        //        System.out.println("PostPattern is " + postPattern);

        Map<String, Integer> freqMap = getFrequency(cpb);
        Map<String, TreeNode> headers = new HashMap<String, TreeNode>();
        for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
            String name = entry.getKey();
            int cnt = entry.getValue();
            //每一次递归时都有可能出现一部分模式的频数低于阈值
            if (cnt >= minSup) {
                TreeNode node = new TreeNode(name);
                node.setCount(cnt);
                headers.put(name, node);
            }
        }

        TreeNode treeRoot = buildSubTree(cpb, freqMap, headers);
        //如果只剩下虚根节点，则递归结束
        if ((treeRoot.getChildren() == null) || (treeRoot.getChildren().size() == 0)) {
            return;
        }

        //如果树是单枝的，则直接把“路径的各种组合+后缀模式”添加到频繁模式集中。这个技巧是可选的，即跳过此步进入下一轮递归也可以得到正确的结果
        if (isSingleBranch(treeRoot)) {
            LinkedList<TreeNode> path = new LinkedList<TreeNode>();
            TreeNode currNode = treeRoot;
            while (currNode.getChildren() != null) {
                currNode = currNode.getChildren().get(0);
                path.add(currNode);
            }
            //调用combine时path不宜过长，否则会OutOfMemory
            if (path.size() <= 20) {
                List<List<TreeNode>> results = new ArrayList<List<TreeNode>>();
                combine(path, results);
                for (List<TreeNode> list : results) {
                    int cnt = 0;
                    List<String> rule = new ArrayList<String>();
                    for (TreeNode node : list) {
                        rule.add(node.getName());
                        cnt = node.getCount();//cnt最FPTree叶节点的计数
                    }
                    if (postModel != null) {
                        rule.addAll(postModel);
                    }
                    //frequentMap.put(rule, cnt);

                }
                return;
            } else {
                System.err.println("length of path is too long: " + path.size());
            }
        }

        for (TreeNode header : headers.values()) {
            List<String> rule = new ArrayList<String>();
            rule.add(header.getName());
            if (postModel != null) {
                rule.addAll(postModel);
            }
            //表头项+后缀模式  构成一条频繁模式（频繁模式内部也是按照F1排序的），频繁度为表头项的计数
            frequentMap.put(rule, header.getCount());
            //新的后缀模式：表头项+上一次的后缀模式（注意保持顺序，始终按F1的顺序排列）
            LinkedList<String> newPostPattern = new LinkedList<String>();
            newPostPattern.add(header.getName());
            if (postModel != null) {
                newPostPattern.addAll(postModel);
            }
            //新的条件模式基
            List<List<String>> newCPB = new LinkedList<List<String>>();
            TreeNode nextNode = header;
            while ((nextNode = nextNode.getNext()) != null) {
                int counter = nextNode.getCount();
                //获得从虚根节点（不包括虚根节点）到当前节点（不包括当前节点）的路径，即一条条件模式基。注意保持顺序：你节点在前，子节点在后，即始终保持频率高的在前
                LinkedList<String> path = new LinkedList<String>();
                TreeNode parent = nextNode;
                while ((parent = parent.getParent()).getName() != null) {//虚根节点的name为null
                    path.push(parent.getName());//往表头插入
                }
                //事务要重复添加counter次
                while (counter-- > 0) {
                    newCPB.add(path);
                }
            }
            growth(newCPB, newPostPattern);
        }
    }
    private TreeNode buildSubTree(List<List<String>> transRecords,
                                  final Map<String, Integer> freqMap,
                                  final Map<String, TreeNode> headers) {
        TreeNode root = new TreeNode();//虚根节点
        for (List<String> transRecord : transRecords) {
            LinkedList<String> record = new LinkedList<String>(transRecord);
            TreeNode subTreeRoot = root;
            TreeNode tmpRoot = null;
            if (root.getChildren() != null) {
                //延已有的分支，令各节点计数加1
                while (!record.isEmpty()
                        && (tmpRoot = subTreeRoot.findChild(record.peek())) != null) {
                    tmpRoot.countIncrement(1);
                    subTreeRoot = tmpRoot;
                    record.poll();
                }
            }
            //长出新的节点
            addNodes(subTreeRoot, record, headers);
        }
        return root;
    }

    private void addNodes(TreeNode ancestor, LinkedList<String> record,
                          final Map<String, TreeNode> headers) {
        while (!record.isEmpty()) {
            String item = (String) record.poll();
            //单个项的出现频数必须大于最小支持数，否则不允许插入FP树。达到最小支持度的项都在headers中。每一次递归根据条件模式基本建立新的FPTree时，把要把频数低于minSuport的排除在外，这也正是FPTree比穷举法快的真正原因
            if (headers.containsKey(item)) {
                TreeNode leafnode = new TreeNode(item);
                leafnode.setCount(1);
                leafnode.setParent(ancestor);
                ancestor.addChild(leafnode);

                TreeNode header = headers.get(item);
                TreeNode tail=header.getTail();
                if(tail!=null){
                    tail.setNext(leafnode);
                }else{
                    header.setNext(leafnode);
                }
                header.setTail(leafnode);
                addNodes(leafnode, record, headers);
            }
        }
    }

    private void generateFrequentPatterns() {
        ArrayList<Pattern> patterns = new ArrayList<>();
        for(List<String> items : frequentMap.keySet()) {
            Pattern pattern = new Pattern();
            pattern.setNum(frequentMap.get(items));
            pattern.setItemSet(new ItemSet(items));
            patterns.add(pattern);
        }
        patterns.sort(new Comparator<Pattern>() {
            @Override
            public int compare(Pattern o1, Pattern o2) {
                int num1 = o1.getItemSet().getSize();
                int num2 = o2.getItemSet().getSize();
                if(num1 < num2) {
                    return -1;
                }
                else if(num1 == num2) {
                    return 0;
                }
                else {
                    return 1;
                }

            }
        });
        int current = 1;
        ArrayList<Pattern> frequentPatterns = new ArrayList<>();
        for(Pattern pattern : patterns) {
            //System.out.println(pattern);
            if(pattern.getItemSet().getSize() == current) {
                frequentPatterns.add(pattern);
            }
            else {
                allFrequentPatterns.add(frequentPatterns);
                frequentPatterns = new ArrayList<>();
                current = pattern.getItemSet().getSize();
                frequentPatterns.add(pattern);
            }
        }
    }

    private void outputFrequentPatterns() {
        try {
            File file = new File(logPath+"/frequent_patterns.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for(int i = 0;i < allFrequentPatterns.size();i++) {
                bufferedWriter.write("--------------------频繁"+(i+1) + "项集---------------------");
                bufferedWriter.newLine();
                for(Pattern pattern : allFrequentPatterns.get(i)) {
                    bufferedWriter.write(pattern.toString());
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<ArrayList<Pattern>> getAllFrequentPatterns() {
        return allFrequentPatterns;
        //return allFrequentPatterns;
    }

    public void excute() {
        init();
        //剔除非频繁项
        //所有事务中的项按照项头表中次序排序
        System.out.println("FP-growth start:" + new Date());
        long start = System.currentTimeMillis();
        for(List<String> transaction : transactions) {
            ArrayList<String> temp = new ArrayList<>();
            for(String str : transaction) {
                boolean exist = false;
                for(TreeNode treeNode : header) {
                    if (treeNode.getName().equals(str)) {
                        exist = true;
                        break;
                    }
                }
                if(exist) {
                    temp.add(str);
                }
            }
            transaction.clear();
            transaction.addAll(temp);
            transaction.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    int index1=0,index2=0;
                    for(int i = 0;i < header.size();i++) {
                        if(header.get(i).getName().equals(o1)) {
                            index1 = i;
                        }
                        if(header.get(i).getName().equals(o2)) {
                            index2 = i;
                        }
                    }
                    if(index1 == index2) {
                        return 0;
                    }
                    else if(index1 < index2) {
                        return -1;
                    }
                    else {
                        return  1;
                    }
                }
            });
            //System.out.println(transaction);
        }

        //建立FP-tree
        //已有了header作为项头表
        //transactions是所有处理好的事务
        growth(transactions, null);
        generateFrequentPatterns();
        long end = System.currentTimeMillis();
        System.out.println("FP-growth end: "+new Date());
        System.out.println("Time cost: "+(end - start)+" ms.");
        System.out.println("Frequent Patterns at "+logPath+"/frequent_patterns.txt");
        outputFrequentPatterns();
    }
}
