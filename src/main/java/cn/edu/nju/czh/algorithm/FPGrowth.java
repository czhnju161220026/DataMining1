package cn.edu.nju.czh.algorithm;

import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth_with_strings.AlgoFPGrowth_Strings;
import cn.edu.nju.czh.preprocessing.ItemSet;
import cn.edu.nju.czh.preprocessing.Pattern;
import cn.edu.nju.czh.preprocessing.Transaction;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;

public class FPGrowth implements Method{
    private ArrayList<Transaction> transactions ;
    private String logPath;
    private double minSupport;
    private ArrayList<ArrayList<Pattern>> allFrequentPatterns = new ArrayList<>();

    @Override
    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    @Override
    public void setMinSup(double minSupport) {
        this.minSupport = minSupport;
    }

    @Override
    public ArrayList<ArrayList<Pattern>> getAllFrequentPatterns() {
        return allFrequentPatterns;
    }

    private void outputFrequentPatterns(String path, int count, ArrayList<Pattern> frequentPatterns) {
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

    @Override
    public void excute() {
        System.out.println("Fp-growth start:" + new Date());
        long start = System.currentTimeMillis();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(logPath+"/temp.txt")));
            for(Transaction transaction : transactions) {
                for(String string : transaction.getItems()) {
                    writer.write(string.replace(' ','_'));
                    writer.write(" ");
                }
                writer.newLine();
            }
            writer.flush();
            writer.close();
            AlgoFPGrowth_Strings fp = new AlgoFPGrowth_Strings();
            fp.runAlgorithm(logPath+"/temp.txt", logPath+"/temp2.txt", minSupport);
            System.out.println("Fp-groth end: "+new Date());
            long end = System.currentTimeMillis();
            System.out.println("Time cost: "+(end- start) +"ms.");
            File file = new File(logPath+"/temp.txt");
            file.delete();

            Scanner scanner = new Scanner(new BufferedReader(new FileReader(logPath+"/temp2.txt")));
            ArrayList<Pattern> patterns = new ArrayList<>();
            while(scanner.hasNextLine()) {
                //System.out.println(scanner.nextLine());
                String[] temp = scanner.nextLine().split("[: ]+");
                ItemSet itemSet = new ItemSet();
                for(int i =0; i < temp.length-1;i++) {
                    itemSet.addItem(temp[i]);
                }
                Pattern pattern = new Pattern();
                pattern.setItemSet(itemSet);
                pattern.setNum(Integer.parseInt(temp[temp.length-1]));
                patterns.add(pattern);
            }
            scanner.close();
            System.out.println(patterns.size());
            File file1 = new File(logPath+"/temp2.txt");
            file1.delete();
            patterns.sort(new Comparator<Pattern>() {
                @Override
                public int compare(Pattern o1, Pattern o2) {
                    int num1 = o1.getItemSet().getSize();
                    int num2 = o2.getItemSet().getSize();
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
            ArrayList<Pattern> frequentPatterns = new ArrayList<>();
            int count =1;
            for(Pattern pattern : patterns) {
                if(pattern.getItemSet().getSize() == count) {
                    frequentPatterns.add(pattern);
                }
                else {
                    count = pattern.getItemSet().getSize();
                    allFrequentPatterns.add(frequentPatterns);
                    frequentPatterns = new ArrayList<>();
                    frequentPatterns.add(pattern);
                }
            }
            allFrequentPatterns.add(frequentPatterns);

            for(int i = 0;i < allFrequentPatterns.size(); i++) {
                outputFrequentPatterns(logPath+"/frequent_patterns.txt",i+1,allFrequentPatterns.get(i));
            }
            System.out.println("Frequent Patterns at "+logPath+"/frequent_patterns.txt");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
