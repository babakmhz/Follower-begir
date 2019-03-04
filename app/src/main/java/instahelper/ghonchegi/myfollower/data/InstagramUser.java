package instahelper.ghonchegi.myfollower.data;

import android.os.Parcel;
import android.os.Parcelable;

public class InstagramUser implements Parcelable {

    String userId;
    String userName;
    String password;
    String token;
    String userFullName;
    String bio;
    String website;
    String mediaCount;
    String followsCount;
    String followByCount;
    String profilePicture;
    boolean outgoing_request;
    boolean incoming_request;
    boolean isFollowing;
    boolean isPrivate;
    boolean isRequested;
    boolean isCheckedRelationBefore = false;
//    boolean isFollowing;
//    boolean Follower;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(String mediaCount) {
        this.mediaCount = mediaCount;
    }

    public String getFollowsCount() {
        return followsCount;
    }

    public void setFollowsCount(String followsCount) {
        this.followsCount = followsCount;
    }

    public String getFollowByCount() {
        return followByCount;
    }

    public void setFollowByCount(String followByCount) {
        this.followByCount = followByCount;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profile_picture) {
        this.profilePicture = profile_picture;
    }

    public InstagramUser() {

    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public boolean isRequested() {
        return isRequested;
    }

    public void setisRequested(boolean isRequested) {
        this.isRequested = isRequested;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isOutgoing_request() {
        return outgoing_request;
    }

    public void setOutgoing_request(boolean outgoing_request) {
        this.outgoing_request = outgoing_request;
    }

    public boolean isIncoming_request() {
        return incoming_request;
    }

    public void setIncoming_request(boolean incoming_request) {
        this.incoming_request = incoming_request;
    }

    public void setIsRequested(boolean isRequested) {
        this.isRequested = isRequested;
    }

    public boolean isCheckedRelationBefore() {
        return isCheckedRelationBefore;
    }

    public void setIsCheckedRelationBefore(boolean isCheckedRelationBefore) {
        this.isCheckedRelationBefore = isCheckedRelationBefore;
    }

    protected InstagramUser(Parcel in) {
        userId = in.readString();
        userName = in.readString();
        password = in.readString();
        token = in.readString();
        userFullName = in.readString();
        bio = in.readString();
        website = in.readString();
        mediaCount = in.readString();
        followsCount = in.readString();
        followByCount = in.readString();
        profilePicture = in.readString();
        outgoing_request = in.readByte() != 0x00;
        incoming_request = in.readByte() != 0x00;
        isPrivate = in.readByte() != 0x00;
        outgoing_request = in.readByte() != 0x00;
        incoming_request = in.readByte() != 0x00;
        isFollowing = in.readByte() != 0x00;
        isCheckedRelationBefore = in.readByte() != 0x00;
        isRequested = in.readByte() != 0x00;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(password);
        dest.writeString(token);
        dest.writeString(userFullName);
        dest.writeString(bio);
        dest.writeString(website);
        dest.writeString(mediaCount);
        dest.writeString(followsCount);
        dest.writeString(followByCount);
        dest.writeString(profilePicture);
        dest.writeByte((byte) (isPrivate ? 0x01 : 0x00));
        dest.writeByte((byte) (isFollowing ? 0x01 : 0x00));
        dest.writeByte((byte) (isCheckedRelationBefore ? 0x01 : 0x00));
        dest.writeByte((byte) (isRequested ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Creator<InstagramUser> CREATOR = new Creator<InstagramUser>() {
        @Override
        public InstagramUser createFromParcel(Parcel in) {
            return new InstagramUser(in);
        }

        @Override
        public InstagramUser[] newArray(int size) {
            return new InstagramUser[size];
        }
    };
}