package cn.edu.nju.czh.algorithm;

import cn.edu.nju.czh.preprocessing.ItemSet;
import cn.edu.nju.czh.preprocessing.Pattern;
import cn.edu.nju.czh.preprocessing.Transaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class DummyMethod implements Method{
    private ArrayList<Transaction> transactions;
    private String logPath;
    //频繁模式
    private ArrayList<Pattern> frequentPatterns = new ArrayList<>();
    private ArrayList<Pattern> nextFrequentPatterns = new ArrayList<>();
    private ArrayList<ArrayList<Pattern>> allFrequentPatterns = new ArrayList<>();

    private int minSup;
    private double relativeMinSup;

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setMinSup(double minSup) {
        this.relativeMinSup = minSup;
        this.minSup = (int)(relativeMinSup * transactions.size()+0.5);
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public ArrayList<ArrayList<Pattern>> getAllFrequentPatterns() {
        return allFrequentPatterns;
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
        //不进行子集测试，直接计数
        //先去重
        ArrayList<ItemSet> tempSet = new ArrayList<>();
        for(ItemSet set:sets) {
            if(tempSet.isEmpty()) {
                tempSet.add(set);
            }
            else {
                boolean exist = false;
                for(ItemSet set1:tempSet) {
                    if(set1.contains(set)) {
                        exist = true;
                        break;
                    }
                }
                if(!exist) {
                    tempSet.add(set);
                }
            }
        }
        sets = tempSet;
        for(ItemSet set:sets) {
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

    private void outputFrequentModes(String path, int count) {
        frequentPatterns.sort(new Comparator<Pattern>() {
            @Override
            public int compare(Pattern o1, Pattern o2) {
                int num1 = o1.getNum();
                int num2 = o2.getNum();
                if(num1 < num2) {
                    return  -1;
                }
                else if(num1 == num2) {
                    return  0;
                }
                else {
                    return  1;
                }
            }
        });
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
            if(num >= minSup) {
                pattern.setNum(num);
                frequentPatterns.add(pattern);
            }
        }
    }

    //进行挖掘
    public void excute() {
        System.out.println("Dummy Method start:" + new Date());
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
        System.out.println("Dummy Method end: "+new Date());
        System.out.println("Time cost: "+(end - start)+" ms.");
        System.out.println("Frequent Patterns at "+logPath+"/frequent_patterns.txt");
    }
}
