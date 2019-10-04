
package com.follow.irani.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("api_token")
    @Expose
    private String apiToken;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Login() {
    }

    /**
     * 
     * @param apiToken
     * @param status
     * @param uuid
     */
    public Login(int status, String uuid, String apiToken) {
        super();
        this.status = status;
        this.uuid = uuid;
        this.apiToken = apiToken;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Login withStatus(int status) {
        this.status = status;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Login withUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public Login withApiToken(String apiToken) {
        this.apiToken = apiToken;
        return this;
    }

}
