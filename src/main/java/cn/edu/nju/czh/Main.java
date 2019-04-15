package cn.edu.nju.czh;

import cn.edu.nju.czh.apriori.Apriori;
import cn.edu.nju.czh.fpgrowth.FpGrowth;
import cn.edu.nju.czh.preprocessing.*;

import java.util.ArrayList;

// Path1 = dataset/GroceryStore/Groceries.csv
// Path2 = dataset/UNIX_usage/USERI/..
public class Main {

    public static void main(String[] args) {
        GroceriesLoader groceriesLoader = new GroceriesLoader();
        UsageLoader usageLoader = new UsageLoader();
        groceriesLoader.loadGroceries("dataset/GroceryStore/Groceries.csv");
        usageLoader.loadUsages("dataset/UNIX_usage");

        Apriori apriori = new Apriori();
        apriori.setTransactions(groceriesLoader.getTransactions());
        apriori.setMinSup(30);
        apriori.setLogPath("output/Apriori/Groceries");
        apriori.excute();
        apriori.setTransactions(usageLoader.getTransactions());
        apriori.setMinSup(50);
        apriori.setLogPath("output/Apriori/Usage");
        apriori.excute();


        FpGrowth fpGrowth = new FpGrowth();
        fpGrowth.setMinSup(30);
        fpGrowth.setTransactions(groceriesLoader.getTransactions());
        fpGrowth.setLogPath("output/FP-growth/Groceries");
        fpGrowth.excute();

    }
}
