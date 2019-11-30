package com.follow.irani.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class InstagramMedia implements Parcelable {

    private String username;
    private String caption;
    private String imageUrl;
    private String standard_resolution_imageUrl;
    private String low_resolution_imageUrl;
    private String videoUrl;
    private int imageHeight;
    private int imageWidth;
    private int likesCount;
    private int commentsCount;
    private String type;
    private String userId;
    private String fullUsername;
    private String profileImageUrl;
    private String createdTime;
    private String MediaLink;
    private String photoId;// media id
    private String explanation;
    private Bitmap photoBitmap;
    private Bitmap userPhotoBitmap;
    private InstagramComment instagramComment;
    private InstagramLocation location;
    private InstagramLike instagramLike;
    private boolean isLiked;
    private List<String> tags;
    private List<UsersInPhoto> usersInPhotos;

    public String getStandard_resolution_imageUrl() {
        return standard_resolution_imageUrl;
    }

    public void setStandard_resolution_imageUrl(String standard_resolution_imageUrl) {
        this.standard_resolution_imageUrl = standard_resolution_imageUrl;
    }

    public String getLow_resolution_imageUrl() {
        return low_resolution_imageUrl;
    }

    public void setLow_resolution_imageUrl(String low_resolution_imageUrl) {
        this.low_resolution_imageUrl = low_resolution_imageUrl;
    }

    public InstagramComment getInstagramComment() {
        return instagramComment;
    }

    public void setInstagramComment(InstagramComment instagramComment) {
        this.instagramComment = instagramComment;
    }

    public InstagramLike getInstagramLike() {
        return instagramLike;
    }

    public void setInstagramLike(InstagramLike instagramLike) {
        this.instagramLike = instagramLike;
    }

//    public List<String> getTags() {
//        return tags;
//    }

//    public void setTags(List<String> tags) {
//        this.tags = tags;
//    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getMediaLink() {
        return MediaLink;
    }

    public void setMediaLink(String mediaLink) {
        this.MediaLink = mediaLink;
    }

//    public Bitmap getPhotoBitmap() {
//        return photoBitmap;
//    }
//
//    public void setPhotoBitmap(Bitmap photoBitmap) {
//        this.photoBitmap = photoBitmap;
//    }
//
//    public Bitmap getUserPhotoBitmap() {
//        return userPhotoBitmap;
//    }
//
//    public void setUserPhotoBitmap(Bitmap userPhotoBitmap) {
//        this.userPhotoBitmap = userPhotoBitmap;
//    }

    public InstagramLocation getLocation() {
        return location;
    }

    public void setLocation(InstagramLocation location) {
        this.location = location;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullUsername() {
        return fullUsername;
    }

    public void setFullUsername(String fullUsername) {
        this.fullUsername = fullUsername;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<UsersInPhoto> getUsersInPhotos() {
        return usersInPhotos;
    }

    public void setUsersInPhotos(List<UsersInPhoto> usersInPhotos) {
        this.usersInPhotos = usersInPhotos;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public InstagramMedia() {

    }

    protected InstagramMedia(Parcel in) {
        username = in.readString();
        caption = in.readString();
        imageUrl = in.readString();
        standard_resolution_imageUrl = in.readString();
        low_resolution_imageUrl = in.readString();
        videoUrl = in.readString();
        imageHeight = in.readInt();
        imageWidth = in.readInt();
        likesCount = in.readInt();
        commentsCount = in.readInt();
        type = in.readString();
        userId = in.readString();
        fullUsername = in.readString();
        profileImageUrl = in.readString();
        createdTime = in.readString();
        MediaLink = in.readString();
        photoId = in.readString();
        explanation = in.readString();
        photoBitmap = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        userPhotoBitmap = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        instagramComment = (InstagramComment) in.readValue(InstagramComment.class.getClassLoader());
        location = (InstagramLocation) in.readValue(InstagramLocation.class.getClassLoader());
        instagramLike = (InstagramLike) in.readValue(InstagramLike.class.getClassLoader());
        isLiked = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            tags = new ArrayList<String>();
            in.readList(tags, String.class.getClassLoader());
        } else {
            tags = null;
        }
        if (in.readByte() == 0x01) {
            usersInPhotos = new ArrayList<UsersInPhoto>();
            in.readList(usersInPhotos, UsersInPhoto.class.getClassLoader());
        } else {
            usersInPhotos = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(caption);
        dest.writeString(imageUrl);
        dest.writeString(standard_resolution_imageUrl);
        dest.writeString(low_resolution_imageUrl);
        dest.writeString(videoUrl);
        dest.writeInt(imageHeight);
        dest.writeInt(imageWidth);
        dest.writeInt(likesCount);
        dest.writeInt(commentsCount);
        dest.writeString(type);
        dest.writeString(userId);
        dest.writeString(fullUsername);
        dest.writeString(profileImageUrl);
        dest.writeString(createdTime);
        dest.writeString(MediaLink);
        dest.writeString(photoId);
        dest.writeString(explanation);
        dest.writeValue(photoBitmap);
        dest.writeValue(userPhotoBitmap);
        dest.writeValue(instagramComment);
        dest.writeValue(location);
        dest.writeValue(instagramLike);
        dest.writeByte((byte) (isLiked ? 0x01 : 0x00));
        if (tags == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(tags);
        }
        if (usersInPhotos == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(usersInPhotos);
        }
    }

    @SuppressWarnings("unused")
    public static final Creator<InstagramMedia> CREATOR = new Creator<InstagramMedia>() {
        @Override
        public InstagramMedia createFromParcel(Parcel in) {
            return new InstagramMedia(in);
        }

        @Override
        public InstagramMedia[] newArray(int size) {
            return new InstagramMedia[size];
        }
    };
}