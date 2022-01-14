/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extraClasses;

/**
 *
 * @author KL_Schweizer
 */
public class InventoryObject {
    private String name;
    private String article;
    private String brand;
    private Double left;
    private Double sold;
    private Double invoiced;
    private Double transfer;

    public InventoryObject() {
        
    }

    public InventoryObject(String name, String article, String brand, Double left, Double sold, Double invoiced, Double transfer) {
        this.name = name;
        this.article = article;
        this.brand = brand;
        this.left = left;
        this.sold = sold;
        this.invoiced = invoiced;
        this.transfer = transfer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getLeft() {
        return left;
    }

    public void setLeft(Double left) {
        this.left = left;
    }

    public Double getSold() {
        return sold;
    }

    public void setSold(Double sold) {
        this.sold = sold;
    }

    public Double getInvoiced() {
        return invoiced;
    }

    public void setInvoiced(Double invoiced) {
        this.invoiced = invoiced;
    }

    public Double getTransfer() {
        return transfer;
    }

    public void setTransfer(Double transfer) {
        this.transfer = transfer;
    }
}
