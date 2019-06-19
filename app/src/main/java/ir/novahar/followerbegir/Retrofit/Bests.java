
package ir.novahar.followerbegir.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Bests {

    @SerializedName("like")
    @Expose
    private List<Like> like = null;
    @SerializedName("comment")
    @Expose
    private List<Comment> comment = null;
    @SerializedName("follow")
    @Expose
    private List<Follow> follow = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Bests() {
    }

    /**
     * 
     * @param comment
     * @param like
     * @param follow
     */
    public Bests(List<Like> like, List<Comment> comment, List<Follow> follow) {
        super();
        this.like = like;
        this.comment = comment;
        this.follow = follow;
    }

    public List<Like> getLike() {
        return like;
    }

    public void setLike(List<Like> like) {
        this.like = like;
    }

    public Bests withLike(List<Like> like) {
        this.like = like;
        return this;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }

    public Bests withComment(List<Comment> comment) {
        this.comment = comment;
        return this;
    }

    public List<Follow> getFollow() {
        return follow;
    }

    public void setFollow(List<Follow> follow) {
        this.follow = follow;
    }

    public Bests withFollow(List<Follow> follow) {
        this.follow = follow;
        return this;
    }

}
