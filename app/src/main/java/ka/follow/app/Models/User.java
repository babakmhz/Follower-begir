package ka.follow.app.Models;

public class User {
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
    Integer isActive;
    String uuid=null;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public User() {
    }

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

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
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

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isRequested() {
        return isRequested;
    }

    public void setRequested(boolean requested) {
        isRequested = requested;
    }

    public boolean isCheckedRelationBefore() {
        return isCheckedRelationBefore;
    }

    public void setCheckedRelationBefore(boolean checkedRelationBefore) {
        isCheckedRelationBefore = checkedRelationBefore;
    }


    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
}
