package cn.edu.nju.czh.fpgrowth;
import cn.edu.nju.czh.preprocessing.ItemSet;
import cn.edu.nju.czh.preprocessing.Pattern;
import cn.edu.nju.czh.preprocessing.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class FpGrowth {
    private ArrayList<Transaction> transactions;
    private ArrayList<Pattern> modesList = new ArrayList<>();
    private int minSup = 30;
    private String logPath;

    public void setMinSup(int minSup) {
        this.minSup = minSup;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    private void init() {
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

        for(String str:hashMap.keySet()) {
            ItemSet itemSet = new ItemSet();
            itemSet.addItem(str);
            Pattern pattern = new Pattern();
            pattern.setItemSet(itemSet);
            int num = hashMap.get(str);
            if(num > minSup) {
                pattern.setNum(num);
                modesList.add(pattern);
            }
        }
        //按照项的出现次数从大到小排序
        Collections.sort(modesList, new Comparator<Pattern>() {
            @Override
            public int compare(Pattern o1, Pattern o2) {
                if(o1.getNum() == o2.getNum()) {
                    return  0;
                }
                else if(o1.getNum() > o2.getNum()) {
                    return  -1;
                }
                else return 1;
            }
        });
        System.out.println(modesList);
    }

    public void excute() {
        init();
    }
}
