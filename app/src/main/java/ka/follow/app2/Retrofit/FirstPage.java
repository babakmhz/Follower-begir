
package ka.follow.app2.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FirstPage {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("like_coin")
    @Expose
    private int likeCoin;
    @SerializedName("follow_coin")
    @Expose
    private int followCoin;
    @SerializedName("app_version")
    @Expose
    private int appVersion;
    @SerializedName("welcome")
    @Expose
    private String welcome;
    @SerializedName("special_banner")
    @Expose
    private SpecialBanner specialBanner;

    /**
     * No args constructor for use in serialization
     * 
     */
    public FirstPage() {
    }

    /**
     * 
     * @param followCoin
     * @param specialBanner
     * @param appVersion
     * @param status
     * @param welcome
     * @param likeCoin
     */
    public FirstPage(int status, int likeCoin, int followCoin, int appVersion, String welcome, SpecialBanner specialBanner) {
        super();
        this.status = status;
        this.likeCoin = likeCoin;
        this.followCoin = followCoin;
        this.appVersion = appVersion;
        this.welcome = welcome;
        this.specialBanner = specialBanner;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public FirstPage withStatus(int status) {
        this.status = status;
        return this;
    }

    public int getLikeCoin() {
        return likeCoin;
    }

    public void setLikeCoin(int likeCoin) {
        this.likeCoin = likeCoin;
    }

    public FirstPage withLikeCoin(int likeCoin) {
        this.likeCoin = likeCoin;
        return this;
    }

    public int getFollowCoin() {
        return followCoin;
    }

    public void setFollowCoin(int followCoin) {
        this.followCoin = followCoin;
    }

    public FirstPage withFollowCoin(int followCoin) {
        this.followCoin = followCoin;
        return this;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public FirstPage withAppVersion(int appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public String getWelcome() {
        return welcome;
    }

    public void setWelcome(String welcome) {
        this.welcome = welcome;
    }

    public FirstPage withWelcome(String welcome) {
        this.welcome = welcome;
        return this;
    }

    public SpecialBanner getSpecialBanner() {
        return specialBanner;
    }

    public void setSpecialBanner(SpecialBanner specialBanner) {
        this.specialBanner = specialBanner;
    }

    public FirstPage withSpecialBanner(SpecialBanner specialBanner) {
        this.specialBanner = specialBanner;
        return this;
    }

}
