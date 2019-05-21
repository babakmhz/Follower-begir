package ka.follow.app2.data;

import android.os.Parcel;
import android.os.Parcelable;

public class InstagramComment implements Parcelable {

    private String text;
    private String username;
    private String profile_picture;
    private String full_name;
    private String created_time;
    private String user_id;
    private String comment_id;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(int String) {
        this.comment_id = comment_id;
    }

    public InstagramComment() {

    }

    protected InstagramComment(Parcel in) {
        text = in.readString();
        username = in.readString();
        profile_picture = in.readString();
        full_name = in.readString();
        created_time = in.readString();
        user_id = in.readString();
        comment_id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(username);
        dest.writeString(profile_picture);
        dest.writeString(full_name);
        dest.writeString(created_time);
        dest.writeString(user_id);
        dest.writeString(comment_id);
    }

    @SuppressWarnings("unused")
    public static final Creator<InstagramComment> CREATOR = new Creator<InstagramComment>() {
        @Override
        public InstagramComment createFromParcel(Parcel in) {
            return new InstagramComment(in);
        }

        @Override
        public InstagramComment[] newArray(int size) {
            return new InstagramComment[size];
        }
    };
}