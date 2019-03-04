package instahelper.ghonchegi.myfollower.Models;

public class Orders {
    private int id;
    private String TrackingCode;
    private String picUrl;
    private String numberOfReceived;
    private String DateTime;
    private int ordered;
    private String type;

    public Orders(int id, String trackingCode, String picUrl, String numberOfReceived, String dateTime, int ordered, String type) {
        this.id = id;
        TrackingCode = trackingCode;
        this.picUrl = picUrl;
        this.numberOfReceived = numberOfReceived;
        DateTime = dateTime;
        this.ordered = ordered;
        this.type = type;
    }

    public int getOrdered() {
        return ordered;
    }

    public void setOrdered(int ordered) {
        this.ordered = ordered;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrackingCode() {
        return TrackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        TrackingCode = trackingCode;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getNumberOfReceived() {
        return numberOfReceived;
    }

    public void setNumberOfReceived(String numberOfReceived) {
        this.numberOfReceived = numberOfReceived;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }
}
