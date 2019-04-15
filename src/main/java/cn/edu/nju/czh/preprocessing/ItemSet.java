package cn.edu.nju.czh.preprocessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    public boolean equals(ItemSet set) {
        return (set.getItems().size() == items.size()) && this.contains(set);
    }

    public int getSize() {
        return items.size();
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

    //求补集
    public ItemSet getComplement(ItemSet set) {
        ItemSet result = new ItemSet();
        ArrayList<String> items1 = set.getItems();
        for(String string:items) {
            boolean exist = false;
            for(String string1:items1) {
                if(string.equals(string1)) {
                    exist = true;
                    break;
                }
            }
            if(!exist) {
                result.addItem(string);
            }
        }
        return result;
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

    public ItemSet() {}

    public ItemSet(List<String> items) {
        this.items.addAll(items);
    }
}
