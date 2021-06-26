package pos_h2_database;

import java.util.ArrayList;

public class Transaction {
    private String id;
    private String t_id;
    private String t_clerk;
    private String date;
    private ArrayList<Item> item; 

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public String getT_clerk() {
        return t_clerk;
    }

    public void setT_clerk(String t_clerk) {
        this.t_clerk = t_clerk;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Item> getItem() {
        return item;
    }

    public void setItem(ArrayList<Item> item) {
        this.item = item;
    }
}