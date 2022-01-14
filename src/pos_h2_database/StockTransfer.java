/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pos_h2_database;

/**
 *
 * @author KL_Schweizer
 */
public class StockTransfer {
    private String id;
    private String stockTransferId;
    private String itemId;
    private String itemQuantity;
    private String date;
    private String storeId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStockTransferId() {
        return stockTransferId;
    }

    public void setStockTransferId(String stockTransferId) {
        this.stockTransferId = stockTransferId;
    }
}
