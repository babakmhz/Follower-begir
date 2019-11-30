package com.follow.irani.instaAPI.rawData;

import android.content.Context;
import android.content.SharedPreferences;

import com.follow.irani.App;
import com.follow.irani.data.InstagramUser;
import com.follow.irani.data.UserData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.securepreferences.SecurePreferences;

class Dummy {
    String savedData;
    boolean mustSave;
}

public class UserAuthentication {

    private final static String FILE_NAME_OLD = "UserAuthentication";
    private final static String FILE_NAME = "UserAuthentication_encrypted";
    @Expose
    public String username;
    @Expose
    public String password;
    @Expose
    public String userId;
    @Expose
    public String token;
    @Expose
    public String uuid = null;
    public boolean loggedIn = false;
    UserData userData = UserData.getInstance();

    private UserAuthentication() {

    }

    public static UserAuthentication FromFile() {
        UserAuthentication authentication = new UserAuthentication();
        authentication.loggedIn = false;
        Dummy dummy = getSavedData(App.context);
        String savedData = dummy.savedData;
        if (savedData != null && savedData.isEmpty() == false) {//something was saved before , let's load it
            Gson gson = getGson();
            try {
                authentication = gson.fromJson(savedData, UserAuthentication.class);
                authentication.loggedIn = authentication.token != null && authentication.token.isEmpty() == false;
                InstagramUser user = new InstagramUser();
                user.setUserName(authentication.username);
                user.setPassword(authentication.password);
                user.setUserId(authentication.userId);
                user.setToken(authentication.token);
                UserData.getInstance().setSelf_user(user);
            } catch (JsonSyntaxException exc) {

            }
        }
        if (dummy.mustSave)
            authentication.SaveToFile();
        return authentication;
    }

    private static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.serializeNulls().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();
        return gson;
    }

    private static Dummy getSavedData(Context appContext) {
        boolean shouldSave = false;
        SharedPreferences secureFile = new SecurePreferences(appContext, "KM/A*&(#y!*(#t252", FILE_NAME);
        String saved = secureFile.getString("data", null);
        if (saved == null || saved.isEmpty()) {
            //reading from old file
            SharedPreferences preferences = appContext.getSharedPreferences(FILE_NAME_OLD, Context.MODE_PRIVATE);
            saved = preferences.getString("data", null);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("data");
            editor.apply();
            shouldSave = true;
        }
        Dummy dummy = new Dummy();
        dummy.savedData = saved;
        dummy.mustSave = shouldSave;
        return dummy;
    }

    public void SaveToFile() {
        SharedPreferences preferences = new SecurePreferences(App.context, "KM/A*&(#y!*(#t252", FILE_NAME);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = getGson();
        try {
            UserAuthentication authentication = new UserAuthentication();
            String savedData = preferences.getString("data", null);
            authentication = gson.fromJson(savedData, UserAuthentication.class);
            authentication.loggedIn = authentication.token != null && !authentication.token.isEmpty();
            InstagramUser user = new InstagramUser();
            user.setUserName(authentication.username);
            user.setPassword(authentication.password);
            user.setUserId(authentication.userId);
            user.setToken(authentication.token);
            UserData.getInstance().setSelf_user(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.putString("data", gson.toJson(this));
        editor.apply();
    }

    public boolean IsLoggedIn() {
        return loggedIn;
    }

    public String GetRankToken(String uuid) {
        return userId + "_" + uuid;
    }

    public void SetToken(String value) {
        token = value;
        loggedIn = token != null && token.isEmpty() == false;
    }

    public String GetToken() {
        return token;
    }

    public void Reset() {
        token = null;
        loggedIn = false;
        SaveToFile();
        SharedPreferences preferences = App.context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.apply();
    }

    public static String getFileNameOld() {
        return FILE_NAME_OLD;
    }

    public static String getFileName() {
        return FILE_NAME;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
}