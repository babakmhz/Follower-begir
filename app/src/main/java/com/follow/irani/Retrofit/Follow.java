
package com.follow.irani.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Follow {

    @SerializedName("follow_count")
    @Expose
    private Integer followCount;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("image_path")
    @Expose
    private String imagePath;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Follow() {
    }

    /**
     * 
     * @param imagePath
     * @param userName
     * @param followCount
     */
    public Follow(Integer followCount, String userName, String imagePath) {
        super();
        this.followCount = followCount;
        this.userName = userName;
        this.imagePath = imagePath;
    }

    public Integer getFollowCount() {
        return followCount;
    }

    public void setFollowCount(Integer followCount) {
        this.followCount = followCount;
    }

    public Follow withFollowCount(Integer followCount) {
        this.followCount = followCount;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Follow withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Follow withImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

}
