package data;

public class Notification {
    public static final String MALL_ALERT = "mallAlert";
    public static final String NORMAL_NOTIFICATION = "normalNotification";
    private int id;
    private String type;
    private String message;
    public Notification(int id, String type, String message) {
        this.id = id;
        this.type = type;
        this.message = message;
    }
    public int getId() {
        return this.id;
    }
    public String getNotificationType() {
        return this.type;
    }
    public String getMessage() {
        return this.message;
    }
}
