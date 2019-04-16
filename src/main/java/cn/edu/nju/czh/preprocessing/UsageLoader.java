package cn.edu.nju.czh.preprocessing;


import java.io.*;
import java.util.*;

public class UsageLoader {
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public void loadUsages(String path) {
        File root = new File(path);
        File[] USER = root.listFiles();
        int tid = 0;
        for (int i = 0; i < USER.length; i++) {
            String filePath = path + "/" + USER[i].getName() + "/sanitized_all.981115184025";
            File file = new File(filePath);
            try {
                Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)));
                boolean newOperator = true;
                ArrayList<String> tokens = new ArrayList<>();
                String operator = "";
                while (scanner.hasNextLine()) {
                    //System.out.println(scanner.nextLine());
                    String Line = scanner.nextLine();
                    if (Line.equals("") || Line.equals("|") || Line.equals("&")) {
                        ;
                    } else if (Line.equals("**SOF**")) {
                        operator = "";
                    } else if (Line.equals("**EOF**")) {
                        if (tokens.size() != 0) {
                            tid++;
                            HashSet<String> tempSet = new HashSet<>();
                            for(String string:tokens) {
                                tempSet.add(string);
                            }
                            tokens.clear();
                            for(String string:tempSet) {
                                tokens.add(string);
                            }
                            transactions.add(new Transaction(tid, tokens));
                            tokens = new ArrayList<>();
                        }
                    } else if (Line.charAt(0) != '<' && Line.charAt(0) != '-') {
                        if (operator.equals("")) {
                            operator = operator + Line;
                        } else {
                            tokens.add(operator);
                            operator = Line;
                        }
                    } else {
                        operator = operator + Line;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
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
            for (Transaction transaction : transactions) {
                for (String s : transaction.getItems()) {
                    if (itemMap.get(s) == null) {
                        itemMap.put(s, count);
                        writer.write("@attribute " + itemMap.get(s)+ "{F, T}");
                        writer.newLine();
                        count++;
                    }
                }
            }
            writer.write("@data");
            writer.newLine();
            for (Transaction transaction : transactions) {
                writer.write("{");
                ArrayList<String> items = transaction.getItems();
                ArrayList<Integer> indicators = new ArrayList<>();
                for (int i = 0; i < items.size(); i++) {
                    indicators.add(itemMap.get(items.get(i)));
                }
                Collections.sort(indicators);
                for (int i = 0; i < indicators.size(); i++) {
                    writer.write("" + indicators.get(i) + " T");
                    if (i != indicators.size() - 1) {
                        writer.write(",");
                    }
                }
                writer.write("}");
                writer.newLine();
            }
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        UsageLoader usageLoader = new UsageLoader();
        usageLoader.loadUsages("dataset/UNIX_usage");
        System.out.println(usageLoader.getTransactions().size());
    }
}
