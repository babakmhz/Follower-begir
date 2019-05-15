
package ka.follow.app.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SpecialBanner {

    @SerializedName("follow_coin")
    @Expose
    private int followCoin;
    @SerializedName("like_coin")
    @Expose
    private int likeCoin;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("special_banner_RSA")
    @Expose
    private String specialBannerRSA;

    /**
     * No args constructor for use in serialization
     * 
     */
    public SpecialBanner() {
    }

    /**
     * 
     * @param followCoin
     * @param specialBannerRSA
     * @param price
     * @param likeCoin
     */
    public SpecialBanner(int followCoin, int likeCoin, int price, String specialBannerRSA) {
        super();
        this.followCoin = followCoin;
        this.likeCoin = likeCoin;
        this.price = price;
        this.specialBannerRSA = specialBannerRSA;
    }

    public int getFollowCoin() {
        return followCoin;
    }

    public void setFollowCoin(int followCoin) {
        this.followCoin = followCoin;
    }

    public SpecialBanner withFollowCoin(int followCoin) {
        this.followCoin = followCoin;
        return this;
    }

    public int getLikeCoin() {
        return likeCoin;
    }

    public void setLikeCoin(int likeCoin) {
        this.likeCoin = likeCoin;
    }

    public SpecialBanner withLikeCoin(int likeCoin) {
        this.likeCoin = likeCoin;
        return this;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public SpecialBanner withPrice(int price) {
        this.price = price;
        return this;
    }

    public String getSpecialBannerRSA() {
        return specialBannerRSA;
    }

    public void setSpecialBannerRSA(String specialBannerRSA) {
        this.specialBannerRSA = specialBannerRSA;
    }

    public SpecialBanner withSpecialBannerRSA(String specialBannerRSA) {
        this.specialBannerRSA = specialBannerRSA;
        return this;
    }

}
