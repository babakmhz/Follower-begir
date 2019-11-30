
package com.follow.irani.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Like {

    @SerializedName("like_count")
    @Expose
    private Integer likeCount;
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
    public Like() {
    }

    /**
     * 
     * @param likeCount
     * @param imagePath
     * @param userName
     */
    public Like(Integer likeCount, String userName, String imagePath) {
        super();
        this.likeCount = likeCount;
        this.userName = userName;
        this.imagePath = imagePath;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Like withLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Like withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Like withImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

}
