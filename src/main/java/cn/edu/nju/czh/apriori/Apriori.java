package cn.edu.nju.czh.apriori;

import cn.edu.nju.czh.preprocessing.ItemSet;
import cn.edu.nju.czh.preprocessing.Mode;
import cn.edu.nju.czh.preprocessing.Transaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Apriori {
    private ArrayList<Transaction> transactions;
    //频繁模式
    private ArrayList<Mode> frequentModes = new ArrayList<>();
    ArrayList<Mode> nextFrequentModes = new ArrayList<>();
    private int minSup = 2;

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setMinSup(int minSup) {
        this.minSup = minSup;
    }

    private boolean testItemSet(ItemSet set) {
        // 测试每个k-1子集
        boolean flag = true;
        ArrayList<String> strs = set.getItems();
        for(int i = 0; i < strs.size();i++) {
            ItemSet subSet = new ItemSet();
            for(int j = 0; j< strs.size();j++) {
                if(j!=i) {
                    subSet.addItem(strs.get(j));
                }
            }

            boolean exist =false;
            for(Mode mode:frequentModes) {
                ItemSet set1 = mode.getItemSet();
                if(set1.contains(subSet)) {
                    exist = true;
                    break;
                }
            }
            if(!exist) {
                flag = false;
                break;
            }
        }
        //TODO：去重处理
        boolean exist = false;
        for(Mode mode:nextFrequentModes) {
            ItemSet set1 = mode.getItemSet();
            if(set1.contains(set)) {
                exist = true;
                break;
            }
        }
        return (flag && !exist);
    }

    private int countItemSet(ItemSet set) {
        int count = 0;
        for(Transaction transaction:transactions) {
            if(transaction.containsItemSet(set)) {
                count ++;
            }
        }
        return count;
    }

    private void linkAndCut() {
        ArrayList<ItemSet> sets = new ArrayList<>(); //暂存连接后的集合
        //TODO:连接步
        for(int i = 0;i < frequentModes.size();i++) {
            for(int j = i+1;j<frequentModes.size();j++) {
                ItemSet set1 = frequentModes.get(i).getItemSet();
                ItemSet set2 = frequentModes.get(j).getItemSet();
                if(set1.linkable(set2)) {
                    sets.add(set1.link(set2));
                }
            }
        }
        //TODO:剪枝步
        //理论上此时sets中全是 k 项集
        //对它们的每个k-1子项集进行测试，它们应该都是已知频繁的
        for(ItemSet set:sets) {
            if(this.testItemSet(set)) {
                int num = this.countItemSet(set);
                if(num >= minSup) {
                    //System.out.println("Find new Mode:"+set+","+num);
                    Mode mode = new Mode();
                    mode.setItemSet(set);
                    mode.setNum(num);
                    nextFrequentModes.add(mode);
                }
            }
        }
    }

    private void outputFrequentModes(String path) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
            for(Mode mode:frequentModes) {
                writer.write(""+mode);
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

    //进行挖掘
    public void excute() {
        //先将事务变成若干1频繁项集
        HashMap<String,Integer> hashMap = new HashMap<>();
        for(Transaction transaction:transactions) {
            for(String str:transaction.getItems()) {
                if(hashMap.get(str)!=null) {
                    hashMap.put(str, hashMap.get(str) + 1);
                }
                else {
                    hashMap.put(str, 1);
                }
            }
        }
        //一频繁
        for(String str:hashMap.keySet()) {
            ItemSet itemSet = new ItemSet();
            itemSet.addItem(str);
            Mode mode = new Mode();
            mode.setItemSet(itemSet);
            int num = hashMap.get(str);
            if(num > minSup) {
                mode.setNum(num);
                frequentModes.add(mode);
            }
        }
        int count = 1;
        outputFrequentModes("output/Groceries/frequent"+count+".txt");

        while (true) {
            linkAndCut();
            count ++;
            //没有生成更多项的频繁集了
            if(nextFrequentModes.size()==0) {
                break;
            }
            else {
                //System.out.println(nextFrequentModes);
                System.out.println("Find "+count+"frequent modes");
                frequentModes = nextFrequentModes;
                nextFrequentModes = new ArrayList<>();
                outputFrequentModes("output/Groceries/frequent"+count+".txt");
            }
        }
    }

    public static void main(String[] args) {

    }

}
