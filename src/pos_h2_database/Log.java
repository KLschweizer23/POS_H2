package pos_h2_database;

public class Log {
    private String id;
    private String salesClerk;
    private String status;
    private String timeIn;
    private String timeOut;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSalesClerk() {
        return salesClerk;
    }

    public void setSalesClerk(String salesClerk) {
        this.salesClerk = salesClerk;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
