package com.follow.nobahar.Manager;

import android.content.Context;

import com.follow.nobahar.WheelPiclerView.LuckyItem;

import org.json.JSONObject;

import com.follow.nobahar.App;
import com.follow.nobahar.data.InstagramUser;

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

    public static String duplicate(String userName, String userId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_name", userName);
            jsonBody.put("user_id", userId);


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

    public static String transferCoins(String value, int type, String receiverUUID) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("value", value);
            jsonBody.put("type", type);
            jsonBody.put("api_token", App.Api_Token);
            jsonBody.put("uuid", App.UUID);
            jsonBody.put("destination_uuid", receiverUUID);

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

    public static String specificTicket(int ticketId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("api_token", App.Api_Token);
            jsonBody.put("uuid", App.UUID);
            jsonBody.put("m_id", ticketId);

        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        return jsonBody.toString();


    }

    public static String setLuckyWheel(LuckyItem luckyItem) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("api_token", App.Api_Token);
            jsonBody.put("uuid", App.UUID);
            jsonBody.put("type", luckyItem.type);
            jsonBody.put("value", luckyItem.topText);

        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        return jsonBody.toString();
    }

    public static String submitOrder(int type, String id, String picUrl, int requstedCount) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("api_token", App.Api_Token);
            jsonBody.put("uuid", App.UUID);
            jsonBody.put("type", type);
            jsonBody.put("type_id", id);
            jsonBody.put("request_count", requstedCount);
            jsonBody.put("remaining_count", requstedCount);
            jsonBody.put("image_path", picUrl);


        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        return jsonBody.toString();
    }

    public static String getOrders(int type) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("api_token", App.Api_Token);
            jsonBody.put("uuid", App.UUID);
            jsonBody.put("type", type);

        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        return jsonBody.toString();

    }

    public static String setSubmit(int type, int t_id) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("api_token", App.Api_Token);
            jsonBody.put("uuid", App.UUID);
            jsonBody.put("type", type);
            jsonBody.put("t_id", t_id);

        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        return jsonBody.toString();
    }

    public static String report(int t_id) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("api_token", App.Api_Token);
            jsonBody.put("uuid", App.UUID);
            jsonBody.put("title", "گزارش محتوای نادرست");
            jsonBody.put("description", "پست شماره :‌" + t_id);
            jsonBody.put("m_id", t_id);


        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        return jsonBody.toString();
    }

    public static String addCoin(int i, String o) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("api_token", App.Api_Token);
            jsonBody.put("uuid", App.UUID);
            jsonBody.put("type", i);
            jsonBody.put("count", AES.encryption(o));
            jsonBody.put("coin", AES.encryption(o));
            jsonBody.put("types", AES.encryption(o));
            jsonBody.put("userName", AES.encryption(o));
            byte[] data = o.getBytes("UTF-8");
            String base64 = android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
            jsonBody.put("amount", base64);

            jsonBody.put("status", AES.encryption(o));
            jsonBody.put("cont", AES.encryption(o));

        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        return jsonBody.toString();
    }

    public static String sendTicket(String title, String message) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("api_token", App.Api_Token);
            jsonBody.put("uuid", App.UUID);
            jsonBody.put("title", title);
            jsonBody.put("description", message);

        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        return jsonBody.toString();
    }

    public static String reply(String title, String message, int messageId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("api_token", App.Api_Token);
            jsonBody.put("uuid", App.UUID);
            jsonBody.put("title", title);
            jsonBody.put("description", message);
            jsonBody.put("m_id", messageId);

        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        return jsonBody.toString();
    }


    public static String validateOfferCode(String code) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("api_token", App.Api_Token);
            jsonBody.put("uuid", App.UUID);
            jsonBody.put("code", code);

        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        return jsonBody.toString();
    }
}
