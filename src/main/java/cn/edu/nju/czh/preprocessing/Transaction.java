package cn.edu.nju.czh.preprocessing;

import java.util.ArrayList;

//事务ID
//包含的内容
public class Transaction {
    private int tid;
    private ArrayList<String> items = new ArrayList<>();

    public Transaction(ArrayList<String> tokens) {
        tid = Integer.parseInt(tokens.get(0));
        for (int i = 1; i < tokens.size(); i++) {
            items.add(tokens.get(i));
        }
    }

    public Transaction(int tid, ArrayList<String> tokens) {
        this.tid = tid;
        items = tokens;
    }

    public String toString() {
        return ""+tid+": "+items;
    }

    public void addItem(String item) {
        items.add(item);
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public int getTid() {
        return tid;
    }

    public boolean containsItemSet(ItemSet set) {
        ArrayList<String> items1 = set.getItems();
        boolean flag = true;
        for (String item : items1) {
            boolean exist = false;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).equals(item)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}
