package cn.edu.nju.czh.algorithm;

import cn.edu.nju.czh.preprocessing.Pattern;
import cn.edu.nju.czh.preprocessing.Transaction;

import java.util.ArrayList;

public interface Method {
    void setTransactions(ArrayList<Transaction> transactions);
    void setLogPath(String logPath);
    void setMinSup(int minSupport);
    ArrayList<ArrayList<Pattern>> getAllFrequentPatterns();
    void excute();
}
