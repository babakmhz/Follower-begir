package com.follow.irani.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserIdParser {

    public List<UserIdsViewModel> parseUserIds(JSONArray response) {
        try {
            JSONObject userObject;

            List<UserIdsViewModel> list = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                userObject = response.getJSONObject(i);
                UserIdsViewModel userIdsViewModel = new UserIdsViewModel(userObject.getLong("pk"));
                list.add(userIdsViewModel);
            }
            return list;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
