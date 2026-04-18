package edu.uga.cs.cs4060_final;

/**
 * This class represents a single shopping item, including the item name,
 * and quantity.
 */
public class ShoppingItem {
    private String key;
    private String itemName;
    private String quantity;

    // constructor
    public ShoppingItem() {
      this.key = null;
      this.itemName = null;
      this.quantity = null;
    }

    public ShoppingItem(String itemName, String quantity) {
        this.key = null;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public String getKey() { return key; }

    public void setKey(String key) {  this.key = key; }

    public String getItemName() { return itemName; }

    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getQuantity() { return quantity; }

    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String toString() { return itemName + " " + quantity; }
}
