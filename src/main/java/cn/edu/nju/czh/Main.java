package cn.edu.nju.czh;

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
        ArrayList<Transaction> transactions1 = groceriesLoader.getTransactions();
        ArrayList<Transaction> transactions2 = usageLoader.getTransactions();
        for(Transaction transaction:transactions1) {
            System.out.println(transaction);
        }
    }
}
