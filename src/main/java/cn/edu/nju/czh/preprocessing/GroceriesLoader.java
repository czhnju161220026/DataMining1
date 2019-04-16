package cn.edu.nju.czh.preprocessing;

import java.io.*;
import java.util.*;
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

    @Deprecated
    public void saveAsArff(String path) {
        TreeMap<String, Integer> itemMap = new TreeMap<>();
        int count = 0;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
            writer.write("@relation shopping");
            writer.newLine();
            for(Transaction transaction : transactions) {
                for(String s : transaction.getItems()) {
                    if(itemMap.get(s)== null) {
                        itemMap.put(s, count);
                        writer.write("@attribute "+s.replace(' ','_') + "{F, T}");
                        writer.newLine();
                        count++;
                    }
                }
            }
            writer.write("@data");
            writer.newLine();
            for(Transaction transaction : transactions) {
                writer.write("{");
                ArrayList<String> items = transaction.getItems();
                ArrayList<Integer> indicators = new ArrayList<>();
                for(int i = 0;i < items.size();i++) {
                    indicators.add(itemMap.get(items.get(i)));
                }
                Collections.sort(indicators);
                for(int i = 0 ;i < indicators.size();i ++) {
                    writer.write(""+indicators.get(i)+" T");
                    if(i != indicators.size()-1) {
                        writer.write(",");
                    }
                }
                writer.write("}");
                writer.newLine();
            }
            writer.flush();
            writer.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GroceriesLoader groceriesLoader = new GroceriesLoader();
        groceriesLoader.loadGroceries("dataset/GroceryStore/Groceries.csv");
    }
}
