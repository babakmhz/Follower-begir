package com.follow.irani.instaAPI;

public class ApiConstants {
    public static final String APP_ID="2158345234266561";
    public static final String APP_SECRET="18d5fa313572d5e8bf0b531ddeb0e39b";
    public static final String REDIRECT_URL="https://instagram.com/";
    public static final String USERID_URL="https://api.instagram.com/oauth/access_token/";
    public static final String BASE_INSTA_GRAPH_URL="https://graph.instagram.com/";
    public static final String GRAPH_API_FIELDS="?fields=id,username&";
    public static final String ACCESS_TOKEN_KEY="access_token";

    public static String getUserInfoUrlFromGraphApi(String user_id,String temp_token){
        return BASE_INSTA_GRAPH_URL+user_id+GRAPH_API_FIELDS+ACCESS_TOKEN_KEY+"="+temp_token;
    }
}
