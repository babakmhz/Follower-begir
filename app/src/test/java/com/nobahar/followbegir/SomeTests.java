package com.nobahar.followbegir;

import android.net.Uri;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.net.URI;

public class SomeTests {

    @Test
    public void parseUserIdJsonTest() throws JSONException {
        Assert.assertEquals(1,1);
    }

    @Test
    public void getCodeFromUrl(){
        String url = "https://www.instagram.com/?code=AQAV0-K4q0PI4_x5ct3SsMjkEkz9V8i9sIb-fanyi30NNC29zEVuVs1SVpoeZjD4gIuM4zkhhYg3sMI1AR05kgiqISC5_-CK9uaJODYksyEg-NT_lywk7RYtAMRPfJaDZCDRJQiDo376zKLNUYbH8vGCbXcXOtl056EZa5PTvb4TiM663Eta1alBO580Ya8bvrTQY0wyXWvnVU2PDsWbPCbkh7TuIl92ULFybEbs4UcMQQ#_";
        //        Uri uri = Uri.parse(url);
//        String code = uri.getEncodedFragment();
//        System.out.print(code);
//        System.out.print(uri);
        url = url.substring(url.lastIndexOf("=") + 1,url.length()-2);
        Assert.assertEquals(url,"AQAV0-K4q0PI4_x5ct3SsMjkEkz9V8i9sIb-fanyi30NNC29zEVuVs1SVpoeZjD4gIuM4zkhhYg3sMI1AR05kgiqISC5_-CK9uaJODYksyEg-NT_lywk7RYtAMRPfJaDZCDRJQiDo376zKLNUYbH8vGCbXcXOtl056EZa5PTvb4TiM663Eta1alBO580Ya8bvrTQY0wyXWvnVU2PDsWbPCbkh7TuIl92ULFybEbs4UcMQQ");
    }

}
