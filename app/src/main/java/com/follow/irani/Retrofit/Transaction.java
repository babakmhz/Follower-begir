
package com.follow.irani.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transaction {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("type_id")
    @Expose
    private String typeId;
    @SerializedName("transaction_id")
    @Expose
    private Integer transactionId;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Transaction() {
    }

    /**
     * 
     * @param transactionId
     * @param imagePath
     * @param status
     * @param typeId
     */
    public Transaction(Boolean status, String imagePath, String typeId, Integer transactionId) {
        super();
        this.status = status;
        this.imagePath = imagePath;
        this.typeId = typeId;
        this.transactionId = transactionId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Transaction withStatus(Boolean status) {
        this.status = status;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Transaction withImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public Transaction withTypeId(String typeId) {
        this.typeId = typeId;
        return this;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Transaction withTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
        return this;
    }

}
