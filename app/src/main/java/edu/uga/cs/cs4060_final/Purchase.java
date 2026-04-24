package edu.uga.cs.cs4060_final;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Purchase implements Serializable {

    private String key;
    private String roommateEmaill;
    private double totalPrice;
    private String datePurchased;
    private List<ShoppingItem> allItems;


    public Purchase() {
        this.key = null;
        this.roommateEmaill = null;
        this.totalPrice = 0.0;
        this.datePurchased = null;
        this.allItems = new ArrayList<>();
    }

    public Purchase(String roomateEmaill, double totalPrice, String datePurchased, List<ShoppingItem> allItems) {
        this.key = null;
        this.roommateEmaill = roomateEmaill;
        this.totalPrice = totalPrice;
        this.datePurchased = datePurchased;
        this.allItems = allItems;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRoommateEmaill() {
        return roommateEmaill;
    }
    public void setRoomateEmaill(String roomateEmaill) {
        this.roommateEmaill = roomateEmaill;
    }
    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDatePurchased() {
        return datePurchased;
    }
    public void setDatePurchased(String datePurchased) {
        this.datePurchased = datePurchased;
    }

    public List<ShoppingItem> getAllItems() {
        return allItems;
    }

    public void setAllItems(List<ShoppingItem> allItems) {
        this.allItems = allItems;
    }
}

