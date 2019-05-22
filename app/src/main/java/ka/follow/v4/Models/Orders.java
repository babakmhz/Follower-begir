package ka.follow.v4.Models;

public class Orders {
    private int id;
    private int TrackingCode;
    private String picUrl;
    private int numberOfReceived;
    private String DateTime;
    private int ordered;
    private int type;

    public Orders(int id, int trackingCode, String picUrl, int numberOfReceived, String dateTime, int ordered, int type) {
        this.id = id;
        TrackingCode = trackingCode;
        this.picUrl = picUrl;
        this.numberOfReceived = numberOfReceived;
        DateTime = dateTime;
        this.ordered = ordered;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrackingCode() {
        return TrackingCode;
    }

    public void setTrackingCode(int trackingCode) {
        TrackingCode = trackingCode;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getNumberOfReceived() {
        return numberOfReceived;
    }

    public void setNumberOfReceived(int numberOfReceived) {
        this.numberOfReceived = numberOfReceived;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public int getOrdered() {
        return ordered;
    }

    public void setOrdered(int ordered) {
        this.ordered = ordered;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
