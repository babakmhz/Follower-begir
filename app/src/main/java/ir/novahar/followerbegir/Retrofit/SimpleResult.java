
package ir.novahar.followerbegir.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SimpleResult {

    @SerializedName("status")
    @Expose
    private Boolean status;

    /**
     * No args constructor for use in serialization
     * 
     */
    public SimpleResult() {
    }

    /**
     * 
     * @param status
     */
    public SimpleResult(Boolean status) {
        super();
        this.status = status;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public SimpleResult withStatus(Boolean status) {
        this.status = status;
        return this;
    }

}
