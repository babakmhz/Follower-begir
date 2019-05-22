package ka.follow.v4.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ka.follow.v4.data.InstagramUser;

public class FriendShipUserParser {

    public HashMap<String, InstagramUser> parsUser(JSONObject response) {
        HashMap<String, InstagramUser> users = new HashMap<>();
        try {
            JSONArray userJsonArray = response.getJSONArray("users");
            for (int i = 0; i < userJsonArray.length(); i++) {
                JSONObject userObject;
                userObject = userJsonArray.getJSONObject(i);
                InstagramUser user = new InstagramUser();
                user.setUserName(userObject.getString("username"));
                user.setUserId(userObject.getString("pk"));
                user.setUserFullName(userObject.getString("full_name"));
                user.setProfilePicture(userObject.getString("profile_pic_url"));
                user.setIsPrivate(userObject.getBoolean("is_private"));
                JSONObject friendship = userObject.getJSONObject("friendship_status");
                user.setIsFollowing(friendship.getBoolean("following"));
                user.setOutgoing_request(friendship.getBoolean("outgoing_request"));
                user.setIncoming_request(friendship.getBoolean("incoming_request"));
                users.put(user.getUserId(), user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;
    }
}