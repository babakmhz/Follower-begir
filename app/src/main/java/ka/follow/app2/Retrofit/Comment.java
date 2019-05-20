
package ka.follow.app2.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("comment_count")
    @Expose
    private Integer commentCount;
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
    public Comment() {
    }

    /**
     * 
     * @param imagePath
     * @param userName
     * @param commentCount
     */
    public Comment(Integer commentCount, String userName, String imagePath) {
        super();
        this.commentCount = commentCount;
        this.userName = userName;
        this.imagePath = imagePath;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Comment withCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Comment withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Comment withImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

}
