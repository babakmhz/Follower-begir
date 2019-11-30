package com.follow.irani.data;

public class FriendStatus {

    private boolean incoming_request;
    private boolean outgoing_request;
    private boolean following;
    private boolean followed_by;
    private boolean blocking;
    private boolean is_private;

    public boolean isIncoming_request() {
        return incoming_request;
    }

    public void setIncoming_request(boolean incoming_request) {
        this.incoming_request = incoming_request;
    }

    public boolean isOutgoing_request() {
        return outgoing_request;
    }

    public void setOutgoing_request(boolean outgoing_request) {
        this.outgoing_request = outgoing_request;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isFollowed_by() {
        return followed_by;
    }

    public void setFollowed_by(boolean followed_by) {
        this.followed_by = followed_by;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public boolean is_private() {
        return is_private;
    }

    public void setIs_private(boolean is_private) {
        this.is_private = is_private;
    }
}