package com.follow.irani.parser;

import com.follow.irani.data.InstagramUser;

import org.json.JSONException;
import org.json.JSONObject;

public class UserParser {

    public InstagramUser parsUser(JSONObject response, boolean isUserObject) {
        InstagramUser user = new InstagramUser();
        try {
            JSONObject userObject;
            if (isUserObject)
                userObject = response;
            else
//                userObject = response.getJSONObject("user");
                userObject = response.getJSONObject("graphql").getJSONObject("user");
            user.setUserName(userObject.getString("username"));
            user.setUserId(userObject.getString("id"));
            user.setUserFullName(userObject.getString("full_name"));
            user.setProfilePicture(userObject.getString("profile_pic_url"));
            user.setIsPrivate(userObject.getBoolean("is_private"));
            user.setBio(userObject.getString("biography"));
            user.setWebsite(userObject.getString("external_url"));
            user.setFollowsCount(String.valueOf(userObject.getJSONObject("edge_follow").getInt("count")));
            user.setFollowByCount(String.valueOf(userObject.getJSONObject("edge_followed_by").getInt("count")));
            user.setMediaCount(String.valueOf(userObject.getJSONObject("edge_owner_to_timeline_media").getInt("count")));
            if (user.isPrivate()) {
                try {
//                    JSONObject friendship = userObject.getJSONObject("friendship_status");
                    user.setIsFollowing(userObject.getBoolean("followed_by_viewer"));
                    user.setisRequested(userObject.getBoolean("requested_by_viewer"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}