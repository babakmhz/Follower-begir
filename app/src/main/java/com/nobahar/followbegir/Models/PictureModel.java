package com.nobahar.followbegir.Models;

public class PictureModel {

    private String id ;
    private String name;
    private String url;
    private String likeCount;

    public PictureModel(String id, String name, String url,String likeCount) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.likeCount=likeCount;
    }


    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
