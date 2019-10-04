package com.follow.irani.parser;

import com.follow.irani.data.InstagramLike;

import org.json.JSONException;
import org.json.JSONObject;

public class LikeParser {

    public InstagramLike parsLike(JSONObject response, boolean isDataObject) {
        InstagramLike like = new InstagramLike();
        try {
            JSONObject dataObject;
            if (isDataObject)
                dataObject = response;
            else
                dataObject = response.getJSONObject("data");
            like.setUser_id(dataObject.getString("id"));
            like.setUsername(dataObject.getString("username"));
            like.setFull_name(dataObject.getString("full_name"));
            like.setProfile_picture(dataObject.getString("profile_picture"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return like;
    }
}