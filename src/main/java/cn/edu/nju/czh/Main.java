package cn.edu.nju.czh;

import cn.edu.nju.czh.algorithm.*;
import cn.edu.nju.czh.preprocessing.*;
import cn.edu.nju.czh.rule.StrongRuleMiner;


import java.util.ArrayList;

// Path1 = dataset/GroceryStore/Groceries.csv
// Path2 = dataset/UNIX_usage/USERI/..
public class Main {
    /*算法类，事务集合， 最小支持度(支持数)，最小置信度，输出目录*/
    public static void test(Method method, ArrayList<Transaction> transactions, double minSup, double minConfidence,String logPath){
        method.setTransactions(transactions);
        method.setMinSup(minSup);
        method.setLogPath(logPath);
        method.excute();
        StrongRuleMiner ruleMiner = new StrongRuleMiner();
        ruleMiner.setTotalSize(transactions.size());
        ruleMiner.setFrequentPatterns(method.getAllFrequentPatterns());
        ruleMiner.setMinConfidence(minConfidence);
        ruleMiner.setLogPath(logPath);
        ruleMiner.excute();
    }

    public static void main(String[] args) {
        GroceriesLoader groceriesLoader = new GroceriesLoader();
        UsageLoader usageLoader = new UsageLoader();
        groceriesLoader.loadGroceries("dataset/GroceryStore/Groceries.csv");
        usageLoader.loadUsages("dataset/UNIX_usage");

        test(new Apriori(), groceriesLoader.getTransactions(), 0.003,0.7,"output/Apriori/Groceries");
        test(new Apriori(), usageLoader.getTransactions(), 0.03,0.7,"output/Apriori/Usage");
        test(new FPGrowth(),groceriesLoader.getTransactions(),0.003,0.7,"output/FP-growth/Groceries");
        test(new FPGrowth(),usageLoader.getTransactions(),0.03,0.7,"output/FP-growth/Usage");
        test(new DummyMethod(),groceriesLoader.getTransactions(), 0.003, 0.7, "output/DummyMethod/Groceries");
        test(new DummyMethod(),usageLoader.getTransactions(),0.03,0.7,"output/DummyMethod/Usage");
    }
}
