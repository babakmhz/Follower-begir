package ka.follow.app.data;

import android.os.Parcel;
import android.os.Parcelable;

public class UsersInPhoto implements Parcelable {

    private double x_position;
    private double y_position;
    private String username;
    private String profile_picture;
    private String full_name;
    private String user_id;

    public double getX_position() {
        return x_position;
    }

    public void setX_position(double x_position) {
        this.x_position = x_position;
    }

    public double getY_position() {
        return y_position;
    }

    public void setY_position(double y_position) {
        this.y_position = y_position;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public UsersInPhoto() {
    }

    protected UsersInPhoto(Parcel in) {
        x_position = in.readDouble();
        y_position = in.readDouble();
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
        dest.writeDouble(x_position);
        dest.writeDouble(y_position);
        dest.writeString(username);
        dest.writeString(profile_picture);
        dest.writeString(full_name);
        dest.writeString(user_id);
    }

    @SuppressWarnings("unused")
    public static final Creator<UsersInPhoto> CREATOR = new Creator<UsersInPhoto>() {
        @Override
        public UsersInPhoto createFromParcel(Parcel in) {
            return new UsersInPhoto(in);
        }

        @Override
        public UsersInPhoto[] newArray(int size) {
            return new UsersInPhoto[size];
        }
    };
}