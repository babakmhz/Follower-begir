
package ka.follow.app.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForceFollow {

    @SerializedName("user_id")
    @Expose
    private String userId;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ForceFollow() {
    }

    /**
     * 
     * @param userId
     */
    public ForceFollow(String userId) {
        super();
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ForceFollow withUserId(String userId) {
        this.userId = userId;
        return this;
    }

}
