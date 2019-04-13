package cn.edu.nju.czh.preprocessing;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class UsageLoader {
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public void loadUsages(String path) {
        File root = new File(path);
        File[] USER = root.listFiles();
        int tid = 0;
        for(int i = 0;i < USER.length;i++) {
            String filePath =path + "/"+USER[i].getName()+"/sanitized_all.981115184025";
            File file = new File(filePath);
            try {
                Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)));
                boolean newOperator = true;
                ArrayList<String> tokens = new ArrayList<>();
                String operator = "";
                while(scanner.hasNextLine()) {
                    //System.out.println(scanner.nextLine());
                    String Line = scanner.nextLine();
                    if(Line.equals("")) {
                        ;
                    }
                    else if(Line.equals("**SOF**")) {
                        operator = "";
                    }
                    else if(Line.equals("**EOF**")) {
                        if(tokens.size()!=0) {
                            tid ++;
                            transactions.add(new Transaction(tid, tokens));
                            tokens = new ArrayList<>();
                        }
                    }
                    else if(Line.charAt(0) !='<') {
                        if(operator.equals("")) {
                            operator = operator + Line;
                        }
                        else {
                            tokens.add(operator);
                            operator = Line;
                        }
                    }
                    else {
                        operator = operator + Line;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public static void main(String[] args) {
        UsageLoader usageLoader = new UsageLoader();
        usageLoader.loadUsages("dataset/UNIX_usage");
    }
}
