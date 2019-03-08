package instahelper.ghonchegi.myfollower.Manager;

import android.content.Context;

import org.json.JSONObject;

import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.data.InstagramUser;

public class JsonManager {
    public static String login(InstagramUser user) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", user.getUserId());
            jsonBody.put("image_path", user.getProfilePicture());
            jsonBody.put("post_count", user.getMediaCount());
            jsonBody.put("follower_count", user.getFollowByCount());
            jsonBody.put("following_count", user.getFollowsCount());
            jsonBody.put("user_name", user.getUserName());

        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        return jsonBody.toString();
    }


    public static String firstPageItems(InstagramUser user, Context context) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", user.getUserId());
            jsonBody.put("image_path", user.getProfilePicture());
            jsonBody.put("post_count", user.getMediaCount());
            jsonBody.put("follower_count", user.getFollowByCount());
            jsonBody.put("following_count", user.getFollowsCount());
            jsonBody.put("user_name", user.getUserName());
            jsonBody.put("api_token", App.Api_Token);
            jsonBody.put("uuid", App.UUID);

        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        return jsonBody.toString();

    }

    public static String exchangeCoins(String value, int type) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("value", value);
            jsonBody.put("type", type);
            jsonBody.put("api_token", App.Api_Token);
            jsonBody.put("uuid", App.UUID);

        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        return jsonBody.toString();


    }

    public static String simpleJson() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("api_token", App.Api_Token);
            jsonBody.put("uuid", App.UUID);

        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        return jsonBody.toString();


    }
}
