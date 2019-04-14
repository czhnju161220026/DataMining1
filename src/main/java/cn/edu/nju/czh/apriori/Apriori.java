package cn.edu.nju.czh.apriori;

import cn.edu.nju.czh.preprocessing.ItemSet;
import cn.edu.nju.czh.preprocessing.Mode;
import cn.edu.nju.czh.preprocessing.Transaction;

import java.util.ArrayList;
import java.util.HashMap;

public class Apriori {
    private ArrayList<Transaction> transactions;
    //频繁模式
    private ArrayList<Mode> frequentModes = new ArrayList<>();
    private int minSup = 2;

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setMinSup(int minSup) {
        this.minSup = minSup;
    }
    //TODO：进行挖掘
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
        //System.out.println(hashMap);
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
        //for(Mode mode:frequentModes) {
        //   System.out.println(mode);
        //}
        while (true) {
            ArrayList<Mode> nextFrequentModes = new ArrayList<>();

        }
    }

    public static void main(String[] args) {

    }

}
