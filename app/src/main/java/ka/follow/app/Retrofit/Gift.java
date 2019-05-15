
package ka.follow.app.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gift {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("follow_coin")
    @Expose
    private Integer followCoin;
    @SerializedName("like_coin")
    @Expose
    private Integer likeCoin;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("amount")
    @Expose
    private Integer amount;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Gift() {
    }

    /**
     * 
     * @param amount
     * @param followCoin
     * @param status
     * @param type
     * @param likeCoin
     */
    public Gift(Boolean status, Integer followCoin, Integer likeCoin, Integer type, Integer amount) {
        super();
        this.status = status;
        this.followCoin = followCoin;
        this.likeCoin = likeCoin;
        this.type = type;
        this.amount = amount;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Gift withStatus(Boolean status) {
        this.status = status;
        return this;
    }

    public Integer getFollowCoin() {
        return followCoin;
    }

    public void setFollowCoin(Integer followCoin) {
        this.followCoin = followCoin;
    }

    public Gift withFollowCoin(Integer followCoin) {
        this.followCoin = followCoin;
        return this;
    }

    public Integer getLikeCoin() {
        return likeCoin;
    }

    public void setLikeCoin(Integer likeCoin) {
        this.likeCoin = likeCoin;
    }

    public Gift withLikeCoin(Integer likeCoin) {
        this.likeCoin = likeCoin;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Gift withType(Integer type) {
        this.type = type;
        return this;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Gift withAmount(Integer amount) {
        this.amount = amount;
        return this;
    }

}
