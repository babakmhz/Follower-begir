package ir.novahar.followerbegir.instaAPI;

import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;

import com.auth0.jwt.internal.org.apache.commons.codec.binary.Hex;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import cz.msebera.android.httpclient.Header;
import ir.novahar.followerbegir.App;
import ir.novahar.followerbegir.data.InstagramUser;
import ir.novahar.followerbegir.data.UserData;
import ir.novahar.followerbegir.parser.FriendShipUserParser;
import ir.novahar.followerbegir.instaAPI.rawData.UserAuthentication;

public class InstagramApi {

    private static final String EXPERIMENTS = "ig_creation_growth_holdout,ig_android_place_typeahead_source,ig_android_enable_share_to_whatsapp,ig_android_video_look_ahead,ig_android_direct_drawing_in_quick_cam_universe,ig_android_inline_gallery_backoff_hours_universe,ig_android_rendering_controls,ig_video_encoding_with_crf,ig_android_refill_suggested_user_megaphone,ig_android_oxygen_dynamic_maps,ig_android_direct_plus_button,ig_android_trending_thumbnail_see_all,ig_android_oxygen_static_maps,ig_android_infinite_scrolling,ig_fbns_blocked,ig_android_http_stack_experiment_christmas,ig_android_full_people_card_in_user_list,ig_android_video_stitching,ig_fbns_push,ig_android_profile_contextual_feed,ig_android_post_auto_retry,ig_fb_event_in_mixed_carousel,ig_android_direct_thread_inline_likes,ig_android_profile_button_look_more_clickable,ig_android_histogram_reporter,ig_fbns_shared_exp,ig_android_post_notification_tooltip_universe,ig_android_peek,ig_android_profanity_filter,ig_android_high_res_upload_2,ig_android_boomerang_entry,ig_android_inline_gallery_universe,ig_android_2fac,ig_android_options_app_invite,ig_android_cluster_browsing_nux,ig_android_bugreporterV2,ig_android_disable_chroma_subsampling,ig_android_feed_multi_row,ig_android_profile_picture_creation,ig_android_media_favorites,ig_android_scroll_perf_sample_rate,ig_android_maps_cluster_overlay,ig_android_inline_gallery_no_backoff_on_launch_universe,ig_android_small_tabby_cat,ig_android_user_search_null_state_entries_number,ig_android_app_ads_deeplink,ig_android_follow_in_search,ig_android_direct_emoji_picker,ig_android_add_to_last_post,ig_android_related_items,ig_android_register_push_token_on_cold_start,ig_android_add_connect_button_on_people_page,ig_android_enable_client_share,ig_android_prefetch_venue_in_composer,ig_android_promoted_posts,ig_android_trending_places,ig_android_video_cache_append,ig_android_trending_immersive_viewer,ig_android_direct_default_to_gallery,ig_android_multi_post_creation_flow,ig_android_private_follow_notif_action_inline_v3,ig_pivot_in_explore,ig_android_app_badging,ig_android_boomerang_feed_attribution,ig_android_feed_experiments,ig_android_swipeable_filters_blacklist,ig_android_default_video_icon,ig_android_direct_video_uploads,ig_android_mentions_invite_v2,ig_android_direct_thread_ui_rewrite_qe";
    private final static String USER_AGENT = "Instagram 8.0.0 Android (18/4.3; 320dpi; 720x1280; Xiaomi; HM 1SW; armani; qcom; en_US)";
    private final static String IG_SIG_KEY = "9b3b9e55988c954e51477da115c58ae82dcae7ac01c735b4443a3c5923cb593a";
    private final static String KEY_VERSION = "4";
    private final static String BASE_URL = "https://i.instagram.com/api/v1/";
    private static InstagramApi _instance;
    private UserAuthentication userAuthentication;
    private String uuid;
    private String deviceId;
    private AsyncHttpClient httpClient;

    public InstagramApi() {
        userAuthentication = UserAuthentication.FromFile();
        if (userAuthentication.uuid != null) {
            this.uuid = userAuthentication.uuid;
        } else {
            GenerateUUID(true);
        }
        try {
            InitDeviceId();
        } catch (Exception exc) {

        }
        BuildHttpClient();
    }

    public static InstagramApi getInstance() {
        if (_instance == null)
            _instance = new InstagramApi();
        return _instance;
    }

    public static String GetSignedData(String rawData) {
        byte[] keyBytes = IG_SIG_KEY.getBytes();
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
        } catch (Exception e) {

        }
        String signed = new String(Hex.encodeHex(mac.doFinal(rawData.getBytes())));
        return signed;
    }

    public static RequestParams GetSignedRequestParams(String rawData) {
        String signed = GetSignedData(rawData);
        RequestParams params = new RequestParams();
        params.put("ig_sig_key_version", KEY_VERSION);
        params.put("signed_body", signed + "." + (rawData));
        return params;
    }

    private String getToken() {
        return userAuthentication.GetToken();
    }

    public boolean IsLoggedIn() {
        return userAuthentication.IsLoggedIn();
    }

    public void Login(final String username, final String password, final ResponseHandler handler) {
        httpClient.get(BASE_URL + "si/fetch_headers/?challenge_type=signup&guid=" + GenerateUUID(false), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        String csrToken = FindInHeaders(headers, "Set-Cookie");
                        final int index = csrToken.indexOf(';');
                        csrToken = csrToken.substring(0, index);
                        JSONObject json = new JSONObject();
                        try {
                            json.put("phone_id", GenerateUUID(true));
                            json.put("device_id", getDeviceId(App.context));
                            json.put("guid", uuid);
                            json.put("username", username);
                            json.put("password", password);
                            json.put("_csrftoken", csrToken);
                            json.put("login_attempt_count", 0);
                        } catch (Exception e) {
                            handler.OnFailure(400, e, null);
                            return;
                        }
                        httpClient.post(BASE_URL + "accounts/login/", GetSignedRequestParams(json.toString()), new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                                        try {
                                            if (response.getString("status").equalsIgnoreCase("fail")) {
                                                handler.OnFailure(statusCode, new InvalidKeyException(), response);
                                                return;
                                            }
                                            userAuthentication.username = username;
                                            userAuthentication.password = password;
                                            userAuthentication.userId = response.getJSONObject("logged_in_user").getString("pk");
                                        } catch (JSONException ignored) {

                                        }
                                        String csrToken2 = FindInHeaders(headers, "Set-Cookie");
                                        int index2 = csrToken2.indexOf(';');
                                        csrToken2 = csrToken2.substring(0, index2);
                                        userAuthentication.SetToken(csrToken2);
                                        userAuthentication.uuid = uuid;
                                        userAuthentication.SaveToFile();
                                        InstagramUser user = UserData.getInstance().getSelf_user();
                                        if (user == null)
                                            user = new InstagramUser();
                                        user.setUserName(username);
                                        user.setUserId(userAuthentication.userId);
                                        user.setPassword(password);
                                        user.setToken(csrToken2);
                                        UserData.getInstance().setSelf_user(user);
                                        SyncFeatures(new SecureHttpApi.ResponseHandler() {
                                            @Override
                                            public void OnSuccess(JSONObject response2) {
                                                handler.OnSuccess(response);
                                            }

                                            @Override
                                            public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        handler.OnFailure(statusCode, throwable, errorResponse);
                                    }
                                }
                        );

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (errorResponse != null)
                            handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                }
        );
    }

    public void GetTimeLine(String MaxId, final ResponseHandler handler) {
        if (!IsLoggedIn()) {
            return;
        }
        String temp = "";
        if (MaxId != null) {
            temp = String.format("&max_id=%s", MaxId);
        }
        String url = String.format("feed/timeline/?rank_token=%s&ranked_content=true", userAuthentication.GetRankToken(uuid)) + temp;
        httpClient.get(BASE_URL + url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
        });
    }

    public void GetTimeLine(final ResponseHandler handler) {
        GetTimeLine(null, handler);
    }

    public void GetRecentActivity(final ResponseHandler handler) {
        if (!IsLoggedIn()) {
            return;
        }
        httpClient.get(BASE_URL + "news/inbox/?", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }
        });
    }

    public void Logout(final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + "accounts/logout/", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        ResetLogin();
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                }
        );
    }

    public void EditMedia(final String mediaId, String Caption, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_uuid", this.uuid);
            jsonObject.put("_uid", userAuthentication.userId);
            jsonObject.put("_csrftoken", userAuthentication.GetToken());
            jsonObject.put("caption_text", Caption);
        } catch (JSONException e) {

        }
        httpClient.post(BASE_URL + String.format("media/%s/edit_media/", mediaId), GetSignedRequestParams(jsonObject.toString()), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                }
        );
    }

    public void GetMediaInfo(String media_id, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_uuid", this.uuid);
            jsonObject.put("_uid", userAuthentication.userId);
            jsonObject.put("_csrftoken", userAuthentication.GetToken());
            jsonObject.put("media_id", media_id);
        } catch (JSONException exc) {

        }
        httpClient.post(BASE_URL + "media/" + media_id + "/info/", GetSignedRequestParams(jsonObject.toString()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handler.OnFailure(statusCode, throwable, errorResponse);
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void DeleteMedia(final String mediaId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_uuid", this.uuid);
            jsonObject.put("_uid", userAuthentication.userId);
            jsonObject.put("_csrftoken", userAuthentication.GetToken());
            jsonObject.put("media_id", mediaId);
        } catch (JSONException e) {

        }
        httpClient.post(BASE_URL + String.format("media/%s/delete/", mediaId), GetSignedRequestParams(jsonObject.toString()), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                }
        );
    }

    public void Comment(final String mediaId, String commentText, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_uuid", this.uuid);
            jsonObject.put("_uid", userAuthentication.userId);
            jsonObject.put("_csrftoken", userAuthentication.GetToken());
            jsonObject.put("comment_text", commentText);
        } catch (JSONException e) {

        }
        httpClient.post(BASE_URL + String.format("media/%s/comment/", mediaId), GetSignedRequestParams(jsonObject.toString()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }
        });
    }

    public void GetUsernameInfo(String userId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + String.format("users/%s/info/", userId), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                handler.OnFailure(statusCode, throwable, null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                JSONObject object = new JSONObject();
                if (responseString != null)
                    try {
                        object.put("message", responseString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                try {
                    object.put("skip", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.OnFailure(statusCode, throwable, object);
            }
        });
    }

    public void GetSelfUsernameInfo(final ResponseHandler handler) throws InstaApiException {
        GetUsernameInfo(userAuthentication.userId.toString(), handler);
    }

    public void GetUserTags(String usernameId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + String.format("usertags/%s/feed/?rank_token=%s&ranked_content=true&", usernameId, userAuthentication.GetRankToken(uuid)),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                }
        );
    }

    public void GetUserTags(String usernameId, String max_id, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + String.format("usertags/%s/feed/?max_id=%s&rank_token=%s&ranked_content=true&", usernameId, max_id, userAuthentication.GetRankToken(uuid)),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                }
        );
    }

    public void GetSelfUserTags(final ResponseHandler handler) throws InstaApiException {
        GetUserTags(userAuthentication.userId.toString(), handler);
    }

    public void GetSelfUserTags(String max_id, final ResponseHandler handler) throws InstaApiException {
        GetUserTags(max_id, userAuthentication.userId.toString(), handler);
    }

    public void TagFeed(String tag, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + String.format("feed/tag/%s/?rank_token=%s&ranked_content=true&", tag, userAuthentication.GetRankToken(uuid)),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                }
        );
    }

    public void TagFeed(String tag, String max_id, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + String.format("feed/tag/%s/?max_id=%s&rank_token=%s&ranked_content=true&", tag, max_id, userAuthentication.GetRankToken(uuid)),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                }
        );
    }

    public void GetMediaLikers(String mediaId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + String.format("media/%s/likers/?", mediaId),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                }
        );
    }

    public void GetGeoMedia(String userId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + String.format("maps/user/%s/", userId),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                }
        );
    }

    public void GetSelfGeoMedia(final ResponseHandler handler) throws InstaApiException {
        GetGeoMedia(userAuthentication.userId, handler);
    }

    public void SearchUsers(String query, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        String url = BASE_URL + String.format("users/search/?ig_sig_key_version=%s&is_typeahead=true&query=%s&rank_token=%s", IG_SIG_KEY, query, userAuthentication.GetRankToken(uuid));
        httpClient.get(url,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                });
    }

    public void SearchUsers(String query, String max_id, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        String url = BASE_URL + String.format("users/search/?max_id=%s&ig_sig_key_version=%s&is_typeahead=true&query=%s&rank_token=%s", max_id, IG_SIG_KEY, query, userAuthentication.GetRankToken(uuid));
        httpClient.get(url,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                });
    }

    public void SearchLocation(String query, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        String url = BASE_URL + String.format("fbsearch/places/?rank_token=%s&query=%s", userAuthentication.GetRankToken(uuid), query);
        httpClient.get(url,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                });
    }

    public void SearchLocation(String query, String max_id, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        String url = BASE_URL + String.format("fbsearch/places/?max_id=%s&rank_token=%s&query=%s", max_id, userAuthentication.GetRankToken(uuid), query);
        httpClient.get(url,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                });
    }

    public void SearchTags(String query, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + String.format("tags/search/?is_typeahead=true&q=%s&rank_token=%s", query, userAuthentication.GetRankToken(uuid)),
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                });
    }

    public void GetUserFeed(String userId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + String.format("feed/user/%s/?rank_token=%s&ranked_content=true&", userId, userAuthentication.GetRankToken(uuid)),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                }
        );
    }

    public void GetUserFeed(String userId, String max_id, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + String.format("feed/user/%s/?max_id=%s&rank_token=%s&ranked_content=true&", userId, max_id, userAuthentication.GetRankToken(uuid)),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                }
        );
    }

    public void GetSelfFeed(final ResponseHandler handler) throws InstaApiException {
        GetUserFeed(userAuthentication.userId, handler);
    }

    public void GetSelfFeed(String max_id, final ResponseHandler handler) throws InstaApiException {
        GetUserFeed(userAuthentication.userId, max_id, handler);
    }

    public void GetPopularFeed(final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + String.format("feed/popular/?people_teaser_supported=1&rank_token=%s&ranked_content=true&", userAuthentication.GetRankToken(uuid)),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                }
        );
    }

    public void GetPopularFeed(String max_id, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + String.format("feed/popular/?max_id=%s&people_teaser_supported=1&rank_token=%s&ranked_content=true&", max_id, userAuthentication.GetRankToken(uuid)),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                }
        );
    }

//    public void GetUserRequesting(String userId,String maxId,final ResponseHandler handler) throws InstaApiException
//    {
//        if(!IsLoggedIn())
//        {
//            throw new InstaApiException("Not Logged In",InstaApiException.REASON_NOTLOGGEDIN);
//        }
//       MyLog.e(InstagramApi.class.getName(), ";;maxId: " + maxId);
//
//        String url = BASE_URL+String.format("friendships/%s/requested/?max_id=%s&ig_sig_key_version=%s&rank_token=%s",
//                userId,maxId,IG_SIG_KEY,userAuthentication.GetRankToken(uuid)
//        );
//
//        httpClient.get(url, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                handler.OnSuccess(response);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                ResetLoginIfRequired(statusCode, throwable, errorResponse);
//                handler.OnFailure(statusCode, throwable, errorResponse);
//            }
//        });
//
//    }
//
//    public void GetUserRequesting2(String userId,String maxId,final ResponseHandler handler) throws InstaApiException
//    {
//        if(!IsLoggedIn())
//        {
//            throw new InstaApiException("Not Logged In",InstaApiException.REASON_NOTLOGGEDIN);
//        }
//       MyLog.e(InstagramApi.class.getName(), ";;maxId: " + maxId);
//
//        String url = BASE_URL+String.format("friendships/%s/request/?ig_sig_key_version=%s&rank_token=%s",
//                userId,IG_SIG_KEY,userAuthentication.GetRankToken(uuid)
//        );
//
//        httpClient.get(url, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                handler.OnSuccess(response);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                ResetLoginIfRequired(statusCode, throwable, errorResponse);
//                handler.OnFailure(statusCode, throwable, errorResponse);
//            }
//        });
//
//    }
//
//    public void GetUserRequesting33(String userId,String maxId,final ResponseHandler handler) throws InstaApiException
//    {
//        if(!IsLoggedIn())
//        {
//            throw new InstaApiException("Not Logged In",InstaApiException.REASON_NOTLOGGEDIN);
//        }
//       MyLog.e(InstagramApi.class.getName(), ";;maxId: " + maxId);
//
//        String url = BASE_URL+String.format("friendships/%s/requests/?ig_sig_key_version=%s&rank_token=%s",
//                userId,IG_SIG_KEY,userAuthentication.GetRankToken(uuid)
//        );
//
//        httpClient.get(url, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                handler.OnSuccess(response);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                ResetLoginIfRequired(statusCode, throwable, errorResponse);
//                handler.OnFailure(statusCode, throwable, errorResponse);
//            }
//        });
//
//    }

    public void GetUserFollowers(String userId, String maxId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        String url = BASE_URL + String.format("friendships/%s/followers/?max_id=%s&ig_sig_key_version=%s&rank_token=%s",
                userId, maxId, IG_SIG_KEY, userAuthentication.GetRankToken(uuid)
        );
        httpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }
        });
    }

    public void GetUserBlockers(String userId, String maxId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        String url = BASE_URL + String.format("friendships/%s/blocker/?ig_sig_key_version=%s&rank_token=%s",
                userId, IG_SIG_KEY, userAuthentication.GetRankToken(uuid)
        );
        httpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void GetUserBlockersw(String userId, String maxId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        String url = BASE_URL + String.format("friendships/%s/blockers/?ig_sig_key_version=%s&rank_token=%s",
                userId, IG_SIG_KEY, userAuthentication.GetRankToken(uuid)
        );
        httpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void GetUserFollowerss(String userId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        String url = BASE_URL + String.format("friendships/status/%s/?ig_sig_key_version=%s&rank_token=%s",
                userId, IG_SIG_KEY, userAuthentication.GetRankToken(uuid)
        );
        httpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void GetUserFollowers(String userId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        String url = BASE_URL + String.format("friendships/%s/followers/?ig_sig_key_version=%s&rank_token=%s",
                userId, IG_SIG_KEY, userAuthentication.GetRankToken(uuid)
        );
        httpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.OnFailure(statusCode, throwable, new JSONObject());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                handler.OnFailure(statusCode, throwable, new JSONObject());
            }
        });
    }

    public void GetSelfUserFollowers(String maxId, final ResponseHandler handler) throws InstaApiException {
        GetUserFollowers(userAuthentication.userId, maxId, handler);
    }

    public void GetSelfUserFollowers(final ResponseHandler handler) throws InstaApiException {
        GetUserFollowers(userAuthentication.userId, handler);
    }

    public void GetUserFollowings(String userId, String maxId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        String url = BASE_URL + String.format("friendships/%s/following/?max_id=%s&ig_sig_key_version=%s&rank_token=%s",
                userId, maxId, IG_SIG_KEY, userAuthentication.GetRankToken(uuid)
        );
        httpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.OnFailure(statusCode, throwable, new JSONObject());
            }
        });
    }

    public void GetUserFollowings(String userId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        String url = BASE_URL + String.format("friendships/%s/following/?ig_sig_key_version=%s&rank_token=%s",
                userId, IG_SIG_KEY, userAuthentication.GetRankToken(uuid)
        );
        httpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.OnFailure(statusCode, throwable, new JSONObject());
            }
        });
    }

    public void GetSelfUserFollowings(String maxID, final ResponseHandler handler) throws InstaApiException {
        GetUserFollowings(userAuthentication.userId, maxID, handler);
    }

    public void GetSelfUserFollowings(final ResponseHandler handler) throws InstaApiException {
        GetUserFollowings(userAuthentication.userId, handler);
    }

    public void Unlike(String mediaId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_uuid", uuid);
            jsonObject.put("_uid", userAuthentication.userId);
            jsonObject.put("_csrftoken", userAuthentication.GetToken());
            jsonObject.put("media_id", mediaId);
        } catch (JSONException exc) {

        }
        String url = BASE_URL + String.format("media/%s/unlike/", mediaId);
        httpClient.post(url, GetSignedRequestParams(jsonObject.toString()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }
        });
    }

    public void Like(String mediaId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_uuid", uuid);
            jsonObject.put("_uid", userAuthentication.userId);
            jsonObject.put("_csrftoken", userAuthentication.GetToken());
            jsonObject.put("media_id", mediaId);
        } catch (JSONException exc) {

        }
        String url = BASE_URL + String.format("media/%s/like/", mediaId);
        httpClient.post(url, GetSignedRequestParams(jsonObject.toString()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }
        });
    }

    public void GetMultipleFriendshipStatus(String[] userIds, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        String url = "friendships/show_many/";
        StringBuilder stringBuilder = new StringBuilder();
        for (String id : userIds) {
            stringBuilder.append(id).append(",");
        }
        if (stringBuilder.length() == 0)
            return;
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        String userIdsFormatted = stringBuilder.toString();
        RequestParams requestParams = new RequestParams();
        requestParams.add("_uuid", this.uuid);
        requestParams.add("user_ids", userIdsFormatted);
        requestParams.add("_csrftoken", getToken());
        httpClient.post(BASE_URL + url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                JSONObject jsonObject = new JSONObject();

                if (responseString != null) {
                    try {
                        jsonObject.put("message", responseString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                handler.OnFailure(statusCode, throwable, jsonObject);
            }
        });
    }

    public void GetMultipleFriendshipStatus(final ResponseHandler handler, String... userIds) throws InstaApiException {
        GetMultipleFriendshipStatus(userIds, handler);
    }

    public void GetMediaComments(String mediaId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + String.format("media/%s/comments/?", mediaId),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                });
    }

    public void GetMediaComments(String mediaId, String max_id, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + String.format("media/%s/comments/?max_id=%s", mediaId, max_id),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }
                });
    }

    public void Follow(final String userId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_uuid", uuid);
            jsonObject.put("_uid", userAuthentication.userId);
            jsonObject.put("user_id", userId);
            jsonObject.put("_csrftoken", userAuthentication.GetToken());
        } catch (JSONException exc) {

        }
        httpClient.post(BASE_URL + String.format("friendships/create/%s/", userId),
                GetSignedRequestParams(jsonObject.toString()),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        JSONObject object = new JSONObject();
                        if (responseString != null)
                            try {
                                object.put("message", responseString);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        try {
                            object.put("skip", true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        handler.OnFailure(statusCode, throwable, object);
                    }
                });
    }

    public void Unfollow(String userId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_uuid", uuid);
            jsonObject.put("_uid", userAuthentication.userId);
            jsonObject.put("user_id", userId);
            jsonObject.put("_csrftoken", userAuthentication.GetToken());
        } catch (JSONException exc) {

        }

        httpClient.post(BASE_URL + String.format("friendships/destroy/%s/", userId),
                GetSignedRequestParams(jsonObject.toString()),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handler.OnSuccess(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ResetLoginIfRequired(statusCode, throwable, errorResponse);
                        handler.OnFailure(statusCode, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });
    }

    public void Block(String userId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_uuid", uuid);
            jsonObject.put("_uid", userAuthentication.userId);
            jsonObject.put("user_id", userId);
            jsonObject.put("_csrftoken", userAuthentication.GetToken());
        } catch (JSONException exc) {

        }
        httpClient.post(BASE_URL + String.format("friendships/block/%s/", userId), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }
        });
    }

    public void Unblock(String userId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_uuid", uuid);
            jsonObject.put("_uid", userAuthentication.userId);
            jsonObject.put("user_id", userId);
            jsonObject.put("_csrftoken", userAuthentication.GetToken());
        } catch (JSONException exc) {

        }
        httpClient.post(BASE_URL + String.format("friendships/unblock/%s/", userId), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }
        });
    }

    public void GetLikedMedia(final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + "feed/liked/?", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }
        });
    }

    public void GetLikedMedia(String max_id, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + String.format("feed/liked/?max_id=%s", max_id), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }
        });
    }

    private String GenerateUUID(boolean keepDashes) {
        Random r = new Random();
        uuid = String.format("%04x%04x-%04x-%04x-%04x-%04x%04x%04x",
                r.nextInt(0xffff), r.nextInt(0xffff),
                r.nextInt(0xffff),
                r.nextInt(0x0fff) | 0x4000,
                r.nextInt(0x3fff) | 0x8000,
                r.nextInt(0xffff),
                r.nextInt(0xffff),
                r.nextInt(0xffff)
        );
        return keepDashes ? uuid : uuid.replace("-", "");
    }

    public void GeFollowingtRecentActivity(final ResponseHandler handler) {
        if (!IsLoggedIn()) {
            return;
        }
        httpClient.get(BASE_URL + "news/?", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }
        });
    }

    public void SyncFeatures() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_uuid", this.uuid);
            jsonObject.put("_uid", userAuthentication.userId);
            jsonObject.put("id", userAuthentication.userId);
            jsonObject.put("_csrftoken", userAuthentication.GetToken());
            jsonObject.put("_csrftoken", userAuthentication.GetToken());
            jsonObject.put("experiments", EXPERIMENTS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpClient.post(BASE_URL + "qe/sync/", GetSignedRequestParams(jsonObject.toString()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });
    }

    public void SyncFeatures(final SecureHttpApi.ResponseHandler handler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_uuid", this.uuid);
            jsonObject.put("_uid", userAuthentication.userId);
            jsonObject.put("id", userAuthentication.userId);
            jsonObject.put("_csrftoken", userAuthentication.GetToken());
            jsonObject.put("experiments", EXPERIMENTS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpClient.post(BASE_URL + "qe/sync/", GetSignedRequestParams(jsonObject.toString()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });
    }

    public void AutoCompleteUserList(final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + "friendships/autocomplete_user_list/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void GetFriendshipStatus(String UserId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        String url = String.format("friendships/show/%s/", UserId);
        httpClient.get(BASE_URL + url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                JSONObject object = new JSONObject();
                if (responseString != null)
                    try {
                        object.put("message", responseString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                try {
                    object.put("skip", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.OnFailure(statusCode, throwable, object);
            }
        });
    }

    public void TimelineFeed(final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + "feed/timeline/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }
        });
    }

    public void MegaphoneLog(final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        httpClient.get(BASE_URL + "megaphone/log/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }
        });
    }

    public void GetHashTagFeed(String hashTag, String maxId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        String endPoint;
        if (maxId == null || maxId.isEmpty()) {
            endPoint = String.format("feed/tag/%s/?rank_token=%s&ranked_content=true&", hashTag, userAuthentication.GetRankToken(uuid));
        } else {
            endPoint = String.format("feed/tag/%s/?max_id=%s&rank_token=%s&ranked_content=true&", maxId, hashTag, userAuthentication.GetRankToken(uuid));
        }
        httpClient.get(BASE_URL + endPoint, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }
        });
    }

    public void GetLocationFeed(String locationId, String maxId, final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        String endPoint;
        if (maxId == null || maxId.isEmpty()) {
            endPoint = String.format("feed/location/%s/?rank_token=%s&ranked_content=true&"
                    , locationId, userAuthentication.GetRankToken(uuid));
        } else {
            endPoint = String.format("feed/location/%s/?max_id=%s&rank_token=%s&ranked_content=true&"
                    , locationId, maxId, userAuthentication.GetRankToken(uuid));
        }
        httpClient.get(BASE_URL + endPoint, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void SetPublicAccount(final ResponseHandler handler) throws InstaApiException {
        if (!IsLoggedIn()) {
            throw new InstaApiException("Not Logged In", InstaApiException.REASON_NOTLOGGEDIN);
        }
        JSONObject object = new JSONObject();
        try {
            object.put("_uuid", uuid);
            object.put("_uid", userAuthentication.userId);
            object.put("_csrftoken", userAuthentication.GetToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpClient.post(BASE_URL + "accounts/set_public/", GetSignedRequestParams(object.toString()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.OnSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ResetLoginIfRequired(statusCode, throwable, errorResponse);
                handler.OnFailure(statusCode, throwable, errorResponse);
            }
        });
    }

    private void InitDeviceId() throws NoSuchAlgorithmException {
        Random r = new Random();
        MessageDigest md = MessageDigest.getInstance("MD5");
        int num = (r.nextInt(9999 + 1000) - 1000);
        String s = Integer.toString(num);
        byte[] digested = md.digest(s.getBytes());
        StringBuffer buffer = new StringBuffer();
        for (byte b :
                digested) {
            buffer.append(String.format("%02x", b));
        }
        String hex = buffer.toString();
        deviceId = "android-" + MySplitStringByLength(hex, 16)[r.nextInt(2)];
    }

    private String[] MySplitStringByLength(String input, int len) {
        int remain_length = input.length();
        int start_index = 0;
        ArrayList<String> resultArray = new ArrayList<>();
        for (; remain_length > len; ) {
            resultArray.add(input.substring(start_index, start_index + len));
            start_index += len;
            remain_length -= len;
        }
        if (remain_length > 0)
            resultArray.add(input.substring(start_index, input.length() - 1));
        String[] result = new String[resultArray.size()];
        for (int i = 0; i < resultArray.size(); i++)
            result[i] = resultArray.get(i);
        return result;
    }

    private String[] SplitStringByLenght(String input, int len) {
        return input.split(String.format("(?<=\\G.{%1$d})", len));
    }

    private void BuildHttpClient() {
        httpClient = new AsyncHttpClient();
        httpClient.addHeader("User-Agent", USER_AGENT);
        PersistentCookieStore persistentCookieStore = new PersistentCookieStore(App.context);
        httpClient.setCookieStore(persistentCookieStore);
    }

    private String FindInHeaders(Header[] headers, String keyName) {
        String finded = null;
        for (Header header :
                headers) {
            if (header.getName().equalsIgnoreCase("Set-Cookie")) {
                int index = header.getValue().indexOf('=');
                finded = header.getValue().substring(index + 1);
                break;
            }
        }
        return finded;
    }

    private void ResetLoginIfRequired(int statusCode, Throwable throwable, JSONObject errorResponse) {
        if (statusCode == 400) {
            if (errorResponse != null) {
                if (errorResponse.has("message")) {
                    try {
                        if (errorResponse.getString("message").equalsIgnoreCase("Sorry, too many requests. Please try again later.")) {
                            ShowTryAgainMessage();
                        } else if (errorResponse.getString("message").equalsIgnoreCase("login_required")) {
                            userAuthentication.Reset();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void ResetLogin() {
        userAuthentication.Reset();
    }

    private void updateRelationShip() {
        try {
            AutoCompleteUserList(new ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    FriendShipUserParser parser = new FriendShipUserParser();
                    HashMap<String, InstagramUser> friendShipUsers = parser.parsUser(response);
                }

                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {

                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }

    private String getDeviceId(Context paramContext) {
        return "android-" + Settings.Secure.getString(paramContext.getContentResolver(), "android_id");
    }

    private void ShowTryAgainMessage() {
        try {
            Toast.makeText(App.context, "سرور اینستاگرام شلوغ است، لطفا مدتی بعد مجددا تلاش کنید.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }
    }

    public interface ResponseHandler {
        public void OnSuccess(JSONObject response);

        public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse);
    }
}