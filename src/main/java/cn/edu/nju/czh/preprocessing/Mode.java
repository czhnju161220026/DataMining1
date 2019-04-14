package cn.edu.nju.czh.preprocessing;

public class Mode {
    private ItemSet itemSet;
    private int num = 0;

    public ItemSet getItemSet() {
        return itemSet;
    }

    public void setItemSet(ItemSet itemSet) {
        this.itemSet = itemSet;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void increase() {
        this.num ++ ;
    }

    @Override
    public String toString() {
        return "Support:"+num+" "+itemSet.toString();
    }
}
