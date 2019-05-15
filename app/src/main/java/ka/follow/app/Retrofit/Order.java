
package ka.follow.app.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("request_count")
    @Expose
    private Integer requestCount;
    @SerializedName("remaining_count")
    @Expose
    private Integer remainingCount;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("tracking_code")
    @Expose
    private String trackingCode;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Order() {
    }

    /**
     * 
     * @param trackingCode
     * @param imagePath
     * @param createdAt
     * @param requestCount
     * @param type
     * @param remainingCount
     */
    public Order(Integer requestCount, Integer remainingCount, String createdAt, Integer type, String imagePath, String trackingCode) {
        super();
        this.requestCount = requestCount;
        this.remainingCount = remainingCount;
        this.createdAt = createdAt;
        this.type = type;
        this.imagePath = imagePath;
        this.trackingCode = trackingCode;
    }

    public Integer getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }

    public Order withRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
        return this;
    }

    public Integer getRemainingCount() {
        return remainingCount;
    }

    public void setRemainingCount(Integer remainingCount) {
        this.remainingCount = remainingCount;
    }

    public Order withRemainingCount(Integer remainingCount) {
        this.remainingCount = remainingCount;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Order withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Order withType(Integer type) {
        this.type = type;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Order withImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public Order withTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
        return this;
    }

}
