package instahelper.ghonchegi.myfollower.parser;

import org.json.JSONException;
import org.json.JSONObject;

import instahelper.ghonchegi.myfollower.data.InstagramUser;

public class UserParser {

    public InstagramUser parsUser(JSONObject response, boolean isUserObject) {
        InstagramUser user = new InstagramUser();
        try {
            JSONObject userObject;
            if (isUserObject)
                userObject = response;
            else
                userObject = response.getJSONObject("user");
            user.setUserName(userObject.getString("username"));
            user.setUserId(userObject.getString("pk"));
            user.setUserFullName(userObject.getString("full_name"));
            user.setProfilePicture(userObject.getString("profile_pic_url"));
            user.setIsPrivate(userObject.getBoolean("is_private"));
            user.setBio(userObject.getString("biography"));
            user.setWebsite(userObject.getString("external_url"));
            user.setFollowsCount(String.valueOf(userObject.getInt("following_count")));
            user.setFollowByCount(String.valueOf(userObject.getInt("follower_count")));
            user.setMediaCount(String.valueOf(userObject.getInt("media_count")));
            if (user.isPrivate()) {
                try {
                    JSONObject friendship = userObject.getJSONObject("friendship_status");
                    user.setIsFollowing(friendship.getBoolean("following"));
                    user.setisRequested(friendship.getBoolean("outgoing_request"));
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