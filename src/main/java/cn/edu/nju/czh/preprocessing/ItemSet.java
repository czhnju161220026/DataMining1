package cn.edu.nju.czh.preprocessing;

import java.util.ArrayList;
import java.util.HashSet;

// ItemSet类
public class ItemSet {
    private ArrayList<String> items = new ArrayList<>();

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public void addItem(String item) {
        items.add(item);
    }

    public boolean contains(ItemSet set) {
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

    public boolean linkable(ItemSet set) {
        int n = items.size();
        //正好有n-1个相同的
        int count = 0;
        for (String item : set.getItems()) {
            for (int i = 0; i < items.size(); i++) {
                if (item.equals(items.get(i))) {
                    count++;
                    break;
                }
            }
        }
        return (count == n - 1);
    }


    public ItemSet link(ItemSet set) {
        HashSet<String> hashSet = new HashSet<>();
        for (String item : items) {
            hashSet.add(item);
        }
        for (String item : set.getItems()) {
            hashSet.add(item);
        }

        ItemSet newSet = new ItemSet();
        for (String item : hashSet) {
            newSet.addItem(item);
        }

        return newSet;
    }

    @Override
    public String toString() {
        return items.toString();
    }

    public static void main(String[] args) {
        HashSet<String> h = new HashSet<>();
        h.add("123");
        h.add("234");
        h.add("123");
        System.out.println(h);
    }
}
