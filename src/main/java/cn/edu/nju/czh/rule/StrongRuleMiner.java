package cn.edu.nju.czh.rule;

import cn.edu.nju.czh.preprocessing.ItemSet;
import cn.edu.nju.czh.preprocessing.Pattern;

import java.io.*;
import java.util.ArrayList;

public class StrongRuleMiner {
    private ArrayList<ArrayList<Pattern>> frequentPatterns = new ArrayList<>();
    private String logPath;
    private double minConfidence;
    private ArrayList<Rule> rules = new ArrayList<>();

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    private int totalSize;

    public void setFrequentPatterns(ArrayList<ArrayList<Pattern>> frequentPatterns) {
        this.frequentPatterns = frequentPatterns;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void setMinConfidence(double minConfidence) {
        this.minConfidence = minConfidence;
    }

    public void excute() {
        //for(ArrayList<Pattern> patterns:frequentPatterns) {
        //    System.out.println(patterns);
        //}
        rules.clear();
        //TODO: 根据频繁模式挖掘强规则
        for(int i = 1;i < frequentPatterns.size();i++) {
            ArrayList<Pattern> patterns = frequentPatterns.get(i);
            for(Pattern pattern : patterns) {
                double support = pattern.getNum();
                ItemSet itemSet = pattern.getItemSet();
                ArrayList<String> items = itemSet.getItems();
                int size = itemSet.getSize();
                int numOfSubset =(int)Math.pow(2,size);
                //对于每个非空真子集
                for(int k=1;k < numOfSubset-1;k++) {
                    ItemSet subset = new ItemSet();
                    int x = k;
                    int flag = 0;
                    int index= 0;
                    while(x!=0) {
                        flag = x&0x1;
                        x >>= 1;
                        if(flag == 1) {
                            subset.addItem(items.get(index));
                        }
                        index++;
                    }
                    //计算子集的支持度
                    double subSupport = 0;
                    int subsetSize = subset.getSize();
                    ArrayList<Pattern> patterns1 = frequentPatterns.get(subsetSize-1);
                    for(Pattern pattern1:patterns1) {
                        if(pattern1.getItemSet().equals(subset)) {
                            subSupport = pattern1.getNum();
                        }
                    }
                    //计算是否超过最小置信度
                    //超过阈值，添加规则
                    double confidence = support / subSupport;
                    if(confidence >= minConfidence) {
                        rules.add(new Rule(subset, itemSet.getComplement(subset),confidence,pattern.getNum(),totalSize));
                    }
                }

            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(logPath+"/rules.txt")));
            for(Rule rule:rules) {
                //System.out.println(rule);
                writer.write(rule.toString());
                writer.newLine();
            }
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Strong rules at "+logPath+"/rules.txt");
        System.out.println("---------------------------------------------------");
    }
}
