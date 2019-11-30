package com.follow.irani;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.follow.irani.Manager.DataBaseHelper;
import com.follow.irani.Retrofit.ApiClient;
import com.follow.irani.Retrofit.ApiInterface;
import com.follow.irani.Retrofit.UserCoin;
import com.follow.irani.ViewModel.UserIdParser;
import com.follow.irani.ViewModel.UserIdsViewModel;
import com.follow.irani.instaAPI.InstaApiException;
import com.follow.irani.instaAPI.InstagramApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static me.cheshmak.android.sdk.advertise.Banner.TAG;

public class CheckUnFollowService extends Service {

    private InstagramApi api;
    List<UserIdsViewModel> onlineFollowers;
    private List<UserIdsViewModel> localFollowers;
    int count = 0;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        api = InstagramApi.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getOnlineFollowingList();


        return START_STICKY;
    }

    private int checkUnFollowers() {
        if (onlineFollowers != null && localFollowers != null)
            for (UserIdsViewModel online : onlineFollowers) {
                for (UserIdsViewModel locals : localFollowers) {
                    if (online.getUserId().equals(locals.getUserId())) {
                        count++;
                    }
                }
            }
        if (localFollowers != null) {
            return localFollowers.size() - count;
        } else return 0;
    }

    private void getOnlineFollowingList() {
        try {
            api.GetSelfUserFollowings(new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    try {
                        onlineFollowers = new UserIdParser().parseUserIds(response.getJSONArray("users"));
                        getOfflineFollowers();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {

                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }

    private void getOfflineFollowers() {
        localFollowers = new DataBaseHelper(getBaseContext()).getAllFollowings();
        int amount = checkUnFollowers();
        if (amount != 0)
            decreaseCoin(amount);
        else
        {
            stopSelf();
            stopForeground(true);
        }


    }

    @Override
    public boolean stopService(Intent name) {
        Log.d(TAG, "stopService: ");
        return super.stopService(name);
    }

    private void decreaseCoin(int amount) {
        ApiClient.getClient();
        ApiInterface apiInterface = ApiClient.retrofit.create(ApiInterface.class);
        amount *= -1 * 3;
        byte[] data = new byte[0];
        try {
            data = String.valueOf(amount).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
        int finalAmount = amount;
        apiInterface.AddCoin(App.UUID, App.Api_Token, "1", base64).enqueue(new Callback<UserCoin>() {
            @Override
            public void onResponse(Call<UserCoin> call, Response<UserCoin> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Toast.makeText(CheckUnFollowService.this, "تعداد" + finalAmount + "سکه به دلیل انفالو از شما کاسته شد", Toast.LENGTH_LONG).show();
                        boolean state = new DataBaseHelper(getBaseContext()).clearFollowingList();
                        stopSelf();
                        stopForeground(true);

                    }
                }
            }

            @Override
            public void onFailure(Call<UserCoin> call, Throwable t) {
                Crashlytics.logException(t);

            }
        });
    }
}
