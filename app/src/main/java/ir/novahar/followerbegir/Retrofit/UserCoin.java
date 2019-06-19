package ir.novahar.followerbegir.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserCoin {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("follow_coin")
    @Expose
    private Integer followCoin;
    @SerializedName("like_coin")
    @Expose
    private Integer likeCoin;

    /**
     * No args constructor for use in serialization
     */
    public UserCoin() {
    }

    public UserCoin(Boolean status, Integer followCoin, Integer likeCoin) {
        this.status = status;
        this.followCoin = followCoin;
        this.likeCoin = likeCoin;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getFollowCoin() {
        return followCoin;
    }

    public void setFollowCoin(Integer followCoin) {
        this.followCoin = followCoin;
    }

    public Integer getLikeCoin() {
        return likeCoin;
    }

    public void setLikeCoin(Integer likeCoin) {
        this.likeCoin = likeCoin;
    }
}
