package instahelper.ghonchegi.myfollower.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import instahelper.ghonchegi.myfollower.data.InstagramLocation;
import instahelper.ghonchegi.myfollower.data.InstagramMedia;
import instahelper.ghonchegi.myfollower.data.UsersInPhoto;

public class MediasParser {

    public ArrayList<InstagramMedia> parseMedias(JSONObject response) {

        JSONArray photosJson = null;
        ArrayList<InstagramMedia> medias = new ArrayList<>();
        try {
            photosJson = response.getJSONArray("items"); //getting the data objects (photo objects) from response
            for (int i = 0; i < photosJson.length(); i++) {
                //Get data object (photo object) from the array at that position
                JSONObject photoJson = photosJson.getJSONObject(i);
                //decode the attributes of json into data model
                InstagramMedia photo = new InstagramMedia();
                photo.setUsername(photoJson.getJSONObject("user").getString("username"));
                try {
                    if (photoJson.getJSONObject("caption") != null) {
                        photo.setCaption(photoJson.getJSONObject("caption").getString("text"));
                    } else {
                        photo.setCaption("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    photo.setCaption("");
                }
                JSONArray images = photoJson.getJSONObject("image_versions2").getJSONArray("candidates");
                photo.setImageUrl(images.getJSONObject(images.length() - 1).getString("url")); // thumb
                photo.setLow_resolution_imageUrl(images.getJSONObject((images.length() - 1) / 2).getString("url"));// low resolution
                photo.setStandard_resolution_imageUrl(images.getJSONObject(0).getString("url"));// best
                photo.setImageWidth(images.getJSONObject(0).getInt("width"));
                photo.setImageHeight(images.getJSONObject(0).getInt("height"));
//            photo.setImageHeight(photoJson.getJSONObject("image_versions2").getJSONObject("thumbnail").getInt("height"));
                photo.setLikesCount(photoJson.getInt("like_count"));
                photo.setUserId(photoJson.getJSONObject("user").getString("pk"));
                String type = photoJson.getInt("media_type") == 1 ? "image" : "video";
                photo.setType(type);
                photo.setProfileImageUrl(photoJson.getJSONObject("user").getString("profile_pic_url"));
//            photo.setUserId(photoJson.getJSONObject("user").getString("id"));
                photo.setFullUsername(photoJson.getJSONObject("user").getString("full_name"));
//                long createdTime = photoJson.getLong("device_timestamp");
                long createdTime = photoJson.getLong("taken_at");
                photo.setCreatedTime(String.valueOf(createdTime));
//            photo.setPhotoId(photoJson.getString("pk"));
                photo.setPhotoId(photoJson.getString("id"));
                photo.setCommentsCount(photoJson.getInt("comment_count"));
                photo.setIsLiked(photoJson.getBoolean("has_liked"));
                if (photo.getType().equals("video")) {
                    JSONArray videos = photoJson.getJSONArray("video_versions");
                    photo.setVideoUrl(videos.getJSONObject(0).getString("url"));
                }
                InstagramLocation location = null;
                try {
                    JSONObject locationObject = photoJson.getJSONObject("location");
                    if (locationObject != null) {
                        location = new InstagramLocation();
                        location.setId(locationObject.getString("pk"));
                        location.setName(locationObject.getString("name"));
                        location.setLat(locationObject.getDouble("lat"));
                        location.setLng(locationObject.getDouble("lng"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                photo.setLocation(location);
                try {
                    JSONArray usersInPhotoArrayObject = photoJson.getJSONObject("usertags").getJSONArray("in");
                    ArrayList<UsersInPhoto> usersInPhotoArrayList = new ArrayList<>();
                    for (int k = 0; k < usersInPhotoArrayObject.length(); k++) {
                        UsersInPhoto usersInPhoto = new UsersInPhoto();
                        JSONObject userObject = usersInPhotoArrayObject.getJSONObject(k).getJSONObject("user");
                        JSONArray posArray = usersInPhotoArrayObject.getJSONObject(k).getJSONArray("position");
                        usersInPhoto.setFull_name(userObject.getString("full_name"));
                        usersInPhoto.setUsername(userObject.getString("username"));
                        usersInPhoto.setUser_id(userObject.getString("pk"));
                        usersInPhoto.setProfile_picture(userObject.getString("profile_pic_url"));
                        usersInPhoto.setX_position((Double) posArray.get(0));
                        usersInPhoto.setY_position((Double) posArray.get(1));
                        usersInPhotoArrayList.add(usersInPhoto);
                    }
                    photo.setUsersInPhotos(usersInPhotoArrayList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Add decoded values to photo array
                medias.add(photo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return medias;
    }

    public ArrayList<InstagramMedia> parseMedias(JSONObject response, boolean have_explanation) {
        JSONArray photosJson = null;
        ArrayList<InstagramMedia> medias = new ArrayList<>();
        try {
            photosJson = response.getJSONArray("items"); //getting the data objects (photo objects) from response
            for (int i = 0; i < photosJson.length(); i++) {
                //Get data object (photo object) from the array at that position
                JSONObject photoJson = photosJson.getJSONObject(i);
                //decode the attributes of json into data model
                InstagramMedia photo = new InstagramMedia();
                photo.setUsername(photoJson.getJSONObject("user").getString("username"));
                try {
                    if (photoJson.getJSONObject("caption") != null) {
                        photo.setCaption(photoJson.getJSONObject("caption").getString("text"));
                    } else {
                        photo.setCaption("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    photo.setCaption("");
                }
                JSONArray images = photoJson.getJSONObject("image_versions2").getJSONArray("candidates");
                photo.setImageUrl(images.getJSONObject(images.length() - 1).getString("url")); // thumb
                photo.setLow_resolution_imageUrl(images.getJSONObject((images.length() - 1) / 2).getString("url"));// low resolution
                photo.setStandard_resolution_imageUrl(images.getJSONObject(1).getString("url"));// best
//            photo.setImageHeight(photoJson.getJSONObject("image_versions2").getJSONObject("thumbnail").getInt("height"));
                photo.setLikesCount(photoJson.getInt("like_count"));
                photo.setUserId(photoJson.getJSONObject("user").getString("pk"));
                try {
                    photo.setExplanation(photoJson.getString("explore_context"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String type = photoJson.getInt("media_type") == 1 ? "image" : "video";
                photo.setType(type);
                photo.setProfileImageUrl(photoJson.getJSONObject("user").getString("profile_pic_url"));
//            photo.setUserId(photoJson.getJSONObject("user").getString("id"));
                photo.setFullUsername(photoJson.getJSONObject("user").getString("full_name"));
                long createdTime = photoJson.getLong("device_timestamp");
                photo.setCreatedTime(String.valueOf(createdTime));
//            photo.setPhotoId(photoJson.getString("pk"));
                photo.setPhotoId(photoJson.getString("id"));
                photo.setCommentsCount(photoJson.getInt("comment_count"));
                photo.setIsLiked(photoJson.getBoolean("has_liked"));
                if (photo.getType().equals("video")) {
                    JSONArray videos = photoJson.getJSONArray("video_versions");
                    photo.setVideoUrl(videos.getJSONObject(0).getString("url"));
                }
                InstagramLocation location = null;
                try {
                    JSONObject locationObject = photoJson.getJSONObject("location");
                    if (locationObject != null) {
                        location = new InstagramLocation();
                        location.setId(locationObject.getString("pk"));
                        location.setName(locationObject.getString("name"));
                        location.setLat(locationObject.getDouble("lat"));
                        location.setLng(locationObject.getDouble("lng"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                photo.setLocation(location);
                try {
                    JSONArray usersInPhotoArrayObject = photoJson.getJSONObject("usertags").getJSONArray("in");
                    ArrayList<UsersInPhoto> usersInPhotoArrayList = new ArrayList<>();
                    for (int k = 0; k < usersInPhotoArrayObject.length(); k++) {
                        UsersInPhoto usersInPhoto = new UsersInPhoto();
                        JSONObject userObject = usersInPhotoArrayObject.getJSONObject(k).getJSONObject("user");
                        JSONArray posArray = usersInPhotoArrayObject.getJSONObject(k).getJSONArray("position");
                        usersInPhoto.setFull_name(userObject.getString("full_name"));
                        usersInPhoto.setUsername(userObject.getString("username"));
                        usersInPhoto.setUser_id(userObject.getString("pk"));
                        usersInPhoto.setProfile_picture(userObject.getString("profile_pic_url"));
                        usersInPhoto.setX_position((Double) posArray.get(0));
                        usersInPhoto.setY_position((Double) posArray.get(1));
                        usersInPhotoArrayList.add(usersInPhoto);
                    }
                    photo.setUsersInPhotos(usersInPhotoArrayList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                medias.add(photo);
            }
            //Add decoded values to photo array
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return medias;
    }
}