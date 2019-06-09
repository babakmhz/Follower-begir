package com.nobahar.followbegir.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.nobahar.followbegir.data.InstagramComment;

public class CommentParser {

    public ArrayList<InstagramComment> parseComments(JSONObject response) {
        ArrayList<InstagramComment> instagramComments = new ArrayList<>();
        JSONArray commentsJsonArray = null;
        try {
            commentsJsonArray = response.getJSONArray("data");
            for (int i = 0; i < commentsJsonArray.length(); i++) {
                JSONObject commentJsonObject = commentsJsonArray.getJSONObject(i);
                InstagramComment comment = new InstagramComment();
                comment.setText(commentJsonObject.getString("text"));
                comment.setCreated_time(commentJsonObject.getString("created_time"));
                comment.setFull_name(commentJsonObject.getJSONObject("from").getString("full_name"));
                comment.setProfile_picture(commentJsonObject.getJSONObject("from").getString("profile_picture"));
                comment.setUser_id(commentJsonObject.getJSONObject("from").getString("id"));
                comment.setUsername(commentJsonObject.getJSONObject("from").getString("username"));
                comment.setComment_id(commentJsonObject.getInt("id"));
                instagramComments.add(comment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return instagramComments;
    }
}