package cn.edu.nju.czh.rule;

import cn.edu.nju.czh.preprocessing.ItemSet;

public class Rule {
    private ItemSet set1;
    private ItemSet set2;
    private double confidence;
    private int support;

    public ItemSet getSet1() {
        return set1;
    }

    public void setSet1(ItemSet set1) {
        this.set1 = set1;
    }

    public ItemSet getSet2() {
        return set2;
    }

    public void setSet2(ItemSet set2) {
        this.set2 = set2;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Rule(ItemSet set1, ItemSet set2, double confidence,int support) {
        this.set1 = set1;
        this.set2 = set2;
        this.confidence = confidence;
        this.support = support;
    }

    @Override
    public String toString() {
        return set1.toString() + " => "+set2.toString() + " 置信度:"+String.format("%.2f",confidence*100)+"%,支持度:"+support;
    }
}
