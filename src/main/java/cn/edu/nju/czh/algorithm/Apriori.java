package cn.edu.nju.czh.algorithm;

import cn.edu.nju.czh.preprocessing.ItemSet;
import cn.edu.nju.czh.preprocessing.Pattern;
import cn.edu.nju.czh.preprocessing.Transaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Apriori implements Method{
    private ArrayList<Transaction> transactions;
    private String logPath;
    //频繁模式
    private ArrayList<Pattern> frequentPatterns = new ArrayList<>();
    private ArrayList<Pattern> nextFrequentPatterns = new ArrayList<>();
    private ArrayList<ArrayList<Pattern>> allFrequentPatterns = new ArrayList<>();
    private int minSup = 2;

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setMinSup(int minSup) {
        this.minSup = minSup;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public ArrayList<ArrayList<Pattern>> getAllFrequentPatterns() {
        return allFrequentPatterns;
    }

    private boolean testItemSet(ItemSet set) {
        // 测试每个k-1子集
        boolean flag = true;
        ArrayList<String> strs = set.getItems();
        for(int i = 0; i < strs.size();i++) {
            ItemSet subSet = new ItemSet();
            for(int j = 0; j< strs.size();j++) {
                if(j!=i) {
                    subSet.addItem(strs.get(j));
                }
            }

            boolean exist =false;
            for(Pattern pattern : frequentPatterns) {
                ItemSet set1 = pattern.getItemSet();
                if(set1.contains(subSet)) {
                    exist = true;
                    break;
                }
            }
            if(!exist) {
                flag = false;
                break;
            }
        }
        //TODO：去重处理
        boolean exist = false;
        for(Pattern pattern : nextFrequentPatterns) {
            ItemSet set1 = pattern.getItemSet();
            if(set1.contains(set)) {
                exist = true;
                break;
            }
        }
        return (flag && !exist);
    }

    private int countItemSet(ItemSet set) {
        int count = 0;
        for(Transaction transaction:transactions) {
            if(transaction.containsItemSet(set)) {
                count ++;
            }
        }
        return count;
    }

    private void linkAndCut() {
        ArrayList<ItemSet> sets = new ArrayList<>(); //暂存连接后的集合
        //TODO:连接步
        for(int i = 0; i < frequentPatterns.size(); i++) {
            for(int j = i+1; j< frequentPatterns.size(); j++) {
                ItemSet set1 = frequentPatterns.get(i).getItemSet();
                ItemSet set2 = frequentPatterns.get(j).getItemSet();
                if(set1.linkable(set2)) {
                    sets.add(set1.link(set2));
                }
            }
        }
        //TODO:剪枝步
        //理论上此时sets中全是 k 项集
        //对它们的每个k-1子项集进行测试，它们应该都是已知频繁的
        for(ItemSet set:sets) {
            if(this.testItemSet(set)) {
                int num = this.countItemSet(set);
                if(num >= minSup) {
                    //System.out.println("Find new Pattern:"+set+","+num);
                    Pattern pattern = new Pattern();
                    pattern.setItemSet(set);
                    pattern.setNum(num);
                    nextFrequentPatterns.add(pattern);
                }
            }
        }
    }

    private void outputFrequentModes(String path, int count) {
        try {
            BufferedWriter writer;
            if(count == 1) {
                writer = new BufferedWriter(new FileWriter(new File(path)));
            }
            else {
                writer = new BufferedWriter(new FileWriter(new File(path), true));
            }
            writer.write("-------------------------频繁"+count+"项集----------------------------");
            writer.newLine();
            for(Pattern pattern : frequentPatterns) {
                writer.write(""+ pattern);
                writer.newLine();
            }
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void init() {
        frequentPatterns.clear();
        nextFrequentPatterns.clear();
        allFrequentPatterns.clear();
        //先将事务变成若干1频繁项集
        HashMap<String,Integer> hashMap = new HashMap<>();
        for(Transaction transaction:transactions) {
            for(String str:transaction.getItems()) {
                if(hashMap.get(str)!=null) {
                    hashMap.put(str, hashMap.get(str) + 1);
                }
                else {
                    hashMap.put(str, 1);
                }
            }
        }
        //一频繁
        for(String str:hashMap.keySet()) {
            ItemSet itemSet = new ItemSet();
            itemSet.addItem(str);
            Pattern pattern = new Pattern();
            pattern.setItemSet(itemSet);
            int num = hashMap.get(str);
            if(num > minSup) {
                pattern.setNum(num);
                frequentPatterns.add(pattern);
            }
        }
    }

    //进行挖掘
    public void excute() {
        System.out.println("Apriori start:" + new Date());
        long start = System.currentTimeMillis();
        init();
        int count = 1;
        outputFrequentModes(logPath+"/frequent_patterns.txt", count);

        while (true) {
            linkAndCut();
            count ++;
            //没有生成更多项的频繁集了
            if(nextFrequentPatterns.size()==0) {
                allFrequentPatterns.add(frequentPatterns);
                break;
            }
            else {
                //System.out.println(nextFrequentPatterns);
                //System.out.println("Find "+count+"frequent modes");
                allFrequentPatterns.add(frequentPatterns);
                frequentPatterns = nextFrequentPatterns;
                nextFrequentPatterns = new ArrayList<>();
                outputFrequentModes(logPath+"/frequent_patterns.txt", count);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Apriori end: "+new Date());
        System.out.println("Time cost: "+(end - start)+" ms.");
        System.out.println("Frequent Patterns at "+logPath+"/frequent_patterns.txt");
    }


}
