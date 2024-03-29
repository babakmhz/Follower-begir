package ka.follow.app2.data;

import android.os.Parcel;
import android.os.Parcelable;

public class InstagramLike implements Parcelable {

    private String username;
    private String profile_picture;
    private String full_name;
    private String user_id;

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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public InstagramLike() {

    }

    protected InstagramLike(Parcel in) {
        username = in.readString();
        profile_picture = in.readString();
        full_name = in.readString();
        user_id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(profile_picture);
        dest.writeString(full_name);
        dest.writeString(user_id);
    }

    @SuppressWarnings("unused")
    public static final Creator<InstagramLike> CREATOR = new Creator<InstagramLike>() {
        @Override
        public InstagramLike createFromParcel(Parcel in) {
            return new InstagramLike(in);
        }

        @Override
        public InstagramLike[] newArray(int size) {
            return new InstagramLike[size];
        }
    };
}