package extraClasses;
public class TransferObject {
    private String name;
    private String brand;
    private String article;
    private Double quantity;
    private Double price;
    private Double totalPrice;

    public TransferObject(String name, String brand, String article, Double quantity, Double price, Double totalPrice) {
        this.name = name;
        this.brand = brand;
        this.article = article;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
    }

    public TransferObject() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

}
