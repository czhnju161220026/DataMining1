package cn.edu.nju.czh;

import cn.edu.nju.czh.algorithm.*;
import cn.edu.nju.czh.preprocessing.*;
import cn.edu.nju.czh.rule.StrongRuleMiner;

import java.util.ArrayList;

// Path1 = dataset/GroceryStore/Groceries.csv
// Path2 = dataset/UNIX_usage/USERI/..
public class Main {

    public static void test(Method method, ArrayList<Transaction> transactions, int minSup, double minConfidence,String logPath){
        method.setTransactions(transactions);
        method.setMinSup(minSup);
        method.setLogPath(logPath);
        method.excute();
        StrongRuleMiner ruleMiner = new StrongRuleMiner();
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

        test(new Apriori(), groceriesLoader.getTransactions(), 30,0.6,"output/Apriori/Groceries");
        test(new Apriori(), usageLoader.getTransactions(), 60,0.6,"output/Apriori/Usage");
        test(new FpGrowth(), groceriesLoader.getTransactions(),30,0.6,"output/FP-growth/Groceries");
        test(new FpGrowth(), usageLoader.getTransactions(),60,0.6,"output/FP-growth/Usage");

    }
}
