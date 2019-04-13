package cn.edu.nju.czh.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Path = dataset/Grocery/Store/Groceries.csv
public class GroceriesLoader {

    private ArrayList<Transaction> transactions = new ArrayList<>();

    public void loadGroceries(String path) {
        try {
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(new File(path))));
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String Line = scanner.nextLine();
                Pattern pattern = Pattern.compile("[^\"^{^}^,]+");
                Matcher matcher = pattern.matcher(Line);
                ArrayList<String> tokens = new ArrayList<>();
                while (matcher.find()) {
                    tokens.add(matcher.group());
                }
                transactions.add(new Transaction(tokens));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public static void main(String[] args) {
        GroceriesLoader groceriesLoader = new GroceriesLoader();
        groceriesLoader.loadGroceries("dataset/GroceryStore/Groceries.csv");
    }
}
