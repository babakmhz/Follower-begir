package instahelper.ghonchegi.myfollower.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Dialog.AccountStatisticsDialog;
import instahelper.ghonchegi.myfollower.Dialog.InstagramAutenticationDialog;
import instahelper.ghonchegi.myfollower.Dialog.ManageAccountsDialog;
import instahelper.ghonchegi.myfollower.Dialog.ReviewOrdersDialog;
import instahelper.ghonchegi.myfollower.Dialog.TransferCoinDialog;
import instahelper.ghonchegi.myfollower.Interface.AccountChangerInterface;
import instahelper.ghonchegi.myfollower.Manager.DataBaseHelper;
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.Manager.SharedPreferences;
import instahelper.ghonchegi.myfollower.Models.User;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.data.InstagramUser;
import instahelper.ghonchegi.myfollower.data.UserData;
import instahelper.ghonchegi.myfollower.databinding.FragmentHomeBinding;
import instahelper.ghonchegi.myfollower.instaAPI.InstaApiException;
import instahelper.ghonchegi.myfollower.instaAPI.InstagramApi;
import instahelper.ghonchegi.myfollower.parser.UserParser;

import static android.content.Context.MODE_PRIVATE;
import static instahelper.ghonchegi.myfollower.App.Base_URL;
import static instahelper.ghonchegi.myfollower.App.requestQueue;

public class HomeFragment extends Fragment implements AccountChangerInterface {


    private View view;
    private FragmentHomeBinding binding;
    private InstagramApi api = InstagramApi.getInstance();
    private UserData userData = UserData.getInstance();
    private ProgressDialog progressDoalog;
    private DataBaseHelper dbHeplper;
    private SQLiteDatabase db;
    private android.content.SharedPreferences shared;
    private android.content.SharedPreferences.Editor editor;
    private String next_max_id;
    private String profilePicURL;
    private AccountChangerInterface callBack;

    public HomeFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_home, container, false);
        view = binding.getRoot();
        dbHeplper = new DataBaseHelper(App.context);
        shared = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        editor = shared.edit();
        callBack = this;

        try {
            dbHeplper.createDatabase();
            dbHeplper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        db = dbHeplper.getWritableDatabase();


        getUserInfo();



        /*SelectPictureDialog selectPictureDialog = new SelectPictureDialog();
        selectPictureDialog.show(getChildFragmentManager(), ":");*/
        binding.tvTransferCoin.setOnClickListener(v -> {
            TransferCoinDialog transferCoinDialog = new TransferCoinDialog();
            transferCoinDialog.show(getChildFragmentManager(), "");
        });

        binding.tvOrders.setOnClickListener(v -> {
            ReviewOrdersDialog transferCoinDialog = new ReviewOrdersDialog();
            transferCoinDialog.show(getChildFragmentManager(), "");
        });

        binding.tvAccountInfo.setOnClickListener(v -> {

            AccountStatisticsDialog accountStatisticsDialog = new AccountStatisticsDialog(profilePicURL);
            accountStatisticsDialog.show(getChildFragmentManager(), "");


        });

        binding.tvManageAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageAccountsDialog dialog = new ManageAccountsDialog(callBack);
                dialog.show(getChildFragmentManager(), "");
            }
        });


        return view;

    }

    public void getUserInfo() {

        App.ProgressDialog(getContext(), "لطفا منتظر بمانید ...");
        try {
            api.GetSelfUsernameInfo(new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    DataBaseHelper dbelper = new DataBaseHelper(getActivity());

                    final InstagramUser user = new UserParser().parsUser(response, false);
                    user.setToken(userData.getSelf_user().getToken());
                    user.setPassword(userData.getSelf_user().getPassword());
                    userData.setSelf_user(user);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            User _user = new User();
                            _user.setIsActive(1);
                            _user.setPassword(user.getPassword());
                            _user.setUserName(user.getUserName());
                            _user.setProfilePicture(user.getProfilePicture());
                            _user.setUserId(user.getUserId());


                            if (dbelper.checkkUser(user)) {
                            } else
                                dbelper.addUser(_user);
                            Picasso.get().load(user.getProfilePicture()).error(R.drawable.app_logo).into(binding.profileImage);
                            Picasso.get().load(user.getProfilePicture()).error(R.drawable.app_logo).into(binding.imageView2);
                            profilePicURL = user.getProfilePicture();
                            App.profilePicURl = user.getProfilePicture();
                            binding.tvMediaCount.setText(user.getMediaCount());
                            binding.tvFollowerCount.setText(user.getFollowByCount());
                            binding.tvFollowingCount.setText(user.getFollowsCount());
                            binding.tvUserName.setText(user.getUserFullName());
                            final String requestBody = JsonManager.login(user);

                            StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "user/login", response1 -> {
                                if (response1 != null) {
                                    try {
                                        JSONObject jsonRootObject = new JSONObject(response1);
                                        App.UUID = jsonRootObject.optString("uuid");
                                        App.Api_Token = jsonRootObject.optString("api_token");
                                        if (jsonRootObject.optInt("status") == 0) {
                                            SharedPreferences sharedPreferences = new SharedPreferences(getActivity());
                                            Toast.makeText(getActivity(), "به موجب اولین ورود شما 10 سکه به شما تعلق گرفت", Toast.LENGTH_SHORT).show();
                                            getUserCoins(user);
                                        } else if (jsonRootObject.optInt("status") == 1) {
                                            getUserCoins(user);

                                        }
                                        reloadData();
                                        App.CancelProgressDialog();


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        App.CancelProgressDialog();

                                    }


                                }
                            }, error -> {
                                Log.i("volley", "onErrorResponse: " + error.toString());
                                App.CancelProgressDialog();

                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    HashMap<String, String> headers = new HashMap<String, String>();
                                    headers.put("Content-Type", "application/json");
                                    return headers;
                                }

                                @Override
                                public byte[] getBody() throws AuthFailureError {
                                    return requestBody == null ? null : requestBody.getBytes();
                                }
                            };
                            request.setTag(this);
                            requestQueue.add(request);

                        }
                    });

                }

                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                    App.CancelProgressDialog();

                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }

    private void getUserCoins(InstagramUser user) {
        final String requestBody = JsonManager.firstPageItems(user, getActivity());

        StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "first_page", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response1);
                    if (jsonRootObject.optInt("status") == 1) {
                        binding.tvLikeCoinCount.setText(jsonRootObject.optInt("follow_coin") + "");
                        binding.tvFollowerCoinCount.setText(jsonRootObject.optInt("like_coin") + "");
                        App.followCoin = jsonRootObject.optInt("follow_coin");
                        App.likeCoin = jsonRootObject.optInt("like_coin");

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, error -> {
            Log.i("volley", "onErrorResponse: " + error.toString());
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return requestBody == null ? null : requestBody.getBytes();
            }
        };
        request.setTag(this);
        requestQueue.add(request);

    }

    public void reloadData() {
        ConnectivityManager cm = (ConnectivityManager) App.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            App.Toast(getActivity(), "دستگاه خود را به اینترنت متصل کنید !");
        } else {
            progressDoalog = new ProgressDialog(getActivity());
            progressDoalog.setMax(100);
            progressDoalog.setMessage("در حال دریافت لیست فالوینگ و فالوور ها از اینستاگرام ، لطفا صبور باشید...");
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDoalog.setProgress(0);
            progressDoalog.setCancelable(false);

            db.execSQL("DELETE FROM followers");
            db.execSQL("DELETE FROM followings");
            if (shared.getBoolean("is_first_reload", true)) {
                db.execSQL("DELETE FROM first_followers");
                db.execSQL("DELETE FROM first_followings");
            }
            editor.putBoolean("reloading", true);
            editor.apply();
            fetchFollowers();
        }
    }

    public void fetchFollowers() {
        try {
            api.GetSelfUserFollowers(new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("users");
                        final int total = shared.getInt("follower_count", 0) + shared.getInt("following_count", 0);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject user = jsonArray.getJSONObject(i);
                            if (shared.getBoolean("is_first_reload", true)) {
                                db.execSQL("INSERT or replace INTO first_followers (userid, username, profile_pic_url) " +
                                        "VALUES('" + user.getString("pk") + "','" + user.getString("username") + "','" + user.getString("profile_pic_url") + "')");
                            }
                            db.execSQL("INSERT or replace INTO followers (userid, username, profile_pic_url) " +
                                    "VALUES('" + user.getString("pk") + "','" + user.getString("username") + "','" + user.getString("profile_pic_url") + "')");
                        }
                        next_max_id = null;
                        try {
                            next_max_id = response.getString("next_max_id");
                        } catch (JSONException e) {
                            next_max_id = null;
                            e.printStackTrace();
                        }
                        fetchNextFollowers();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reloadFail();
                        }
                    });
                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }

    public void fetchNextFollowers() {
        if ((next_max_id == null || next_max_id.equals(null)) || next_max_id.equals("null")) {
            fetchFollowing();
        } else {
            try {
                api.GetSelfUserFollowers(next_max_id, new InstagramApi.ResponseHandler() {
                    @Override
                    public void OnSuccess(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("users");
                            final int total = shared.getInt("follower_count", 0) + shared.getInt("following_count", 0);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject user = jsonArray.getJSONObject(i);
                                if (shared.getBoolean("is_first_reload", true)) {
                                    db.execSQL("INSERT or replace INTO first_followers (userid, username, profile_pic_url) " +
                                            "VALUES('" + user.getString("pk") + "','" + user.getString("username") + "','" + user.getString("profile_pic_url") + "')");
                                }
                                db.execSQL("INSERT or replace INTO followers (userid, username, profile_pic_url) " +
                                        "VALUES('" + user.getString("pk") + "','" + user.getString("username") + "','" + user.getString("profile_pic_url") + "')");
                            }
                            next_max_id = null;
                            try {
                                next_max_id = response.getString("next_max_id");
                            } catch (JSONException e) {
                                next_max_id = null;
                                e.printStackTrace();
                            }
                            fetchNextFollowers();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reloadFail();
                            }
                        });
                    }
                });
            } catch (InstaApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void fetchFollowing() {
        try {
            api.GetSelfUserFollowings(new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("users");
                        final int total = shared.getInt("follower_count", 0) + shared.getInt("following_count", 0);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject user = jsonArray.getJSONObject(i);
                            if (shared.getBoolean("is_first_reload", true)) {
                                db.execSQL("INSERT or replace INTO first_followings (userid, username, profile_pic_url) " +
                                        "VALUES('" + user.getString("pk") + "','" + user.getString("username") + "','" + user.getString("profile_pic_url") + "')");
                            }
                            db.execSQL("INSERT or replace INTO followings (userid, username, profile_pic_url) " +
                                    "VALUES('" + user.getString("pk") + "','" + user.getString("username") + "','" + user.getString("profile_pic_url") + "')");
                        }
                        next_max_id = null;
                        try {
                            next_max_id = response.getString("next_max_id");
                        } catch (JSONException e) {
                            next_max_id = null;
                            e.printStackTrace();
                        }
                        fetchNextFollowing();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reloadFail();
                        }
                    });
                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }

    public void fetchNextFollowing() {
        if ((next_max_id == null || next_max_id.equals(null)) || next_max_id.equals("null")) {
            if (shared.getBoolean("is_first_reload", true)) {
                editor.putBoolean("is_first_reload", false);
                editor.putLong("first_reload_time", System.currentTimeMillis());
                editor.apply();
            }
            editor.putBoolean("reloading", false);
            editor.putLong("last_reload_time", System.currentTimeMillis());
            editor.apply();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDoalog.dismiss();
                    //setCountsAndDates();
                }
            });
            return;
        }
        try {
            api.GetSelfUserFollowings(next_max_id, new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("users");
                        final int total = shared.getInt("follower_count", 0) + shared.getInt("following_count", 0);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject user = jsonArray.getJSONObject(i);
                            if (shared.getBoolean("is_first_reload", true)) {
                                db.execSQL("INSERT or replace INTO first_followings (userid, username, profile_pic_url) " +
                                        "VALUES('" + user.getString("pk") + "','" + user.getString("username") + "','" + user.getString("profile_pic_url") + "')");
                            }
                            db.execSQL("INSERT or replace INTO followings (userid, username, profile_pic_url) " +
                                    "VALUES('" + user.getString("pk") + "','" + user.getString("username") + "','" + user.getString("profile_pic_url") + "')");
                        }
                        next_max_id = null;
                        try {
                            next_max_id = response.getString("next_max_id");
                        } catch (JSONException e) {
                            next_max_id = null;
                            e.printStackTrace();
                        }
                        fetchNextFollowing();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reloadFail();
                            }
                        });
                    }
                }

                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reloadFail();
                        }
                    });
                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }

    public void reloadFail() {
        db.execSQL("DELETE FROM followers");
        db.execSQL("DELETE FROM followings");
        if (shared.getBoolean("is_first_reload", true)) {
            db.execSQL("DELETE FROM first_followers");
            db.execSQL("DELETE FROM first_followings");
        }
        if (progressDoalog != null) {
            progressDoalog.dismiss();
        }
        App.Toast(getActivity(), "بروزرسانی اطلاعات با شکست مواجه شد ، لطفا مجددا تلاش نمایید");
//        final Dialog dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.setContentView(R.layout.dialog);
//        TextView txtDialogMessage = (TextView) dialog.findViewById(R.id.txtDialogMessage);
//        TextView txtDialogOk = (TextView) dialog.findViewById(R.id.txtDialogOk);
//        TextView txtDialogCancel = (TextView) dialog.findViewById(R.id.txtDialogCancel);
//        txtDialogMessage.setText("بروزرسانی اطلاعات با شکست مواجه شد ، لطفا مجددا تلاش نمایید !");
//        txtDialogOk.setText("بروزرسانی اطلاعات");
//        txtDialogCancel.setText("خروج از برنامه");
//        txtDialogCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.cancel();
//                finish();
//            }
//        });
//        txtDialogOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.cancel();
//                reloadData();
//            }
//        });
//        dialog.show();
//    }
//
//    public void setCountsAndDates() {
//        long diffLastReload = System.currentTimeMillis() - shared.getLong("last_reload_time", 0);
//        long diffLastReloadInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffLastReload);
//        long diffLastReloadInHours = TimeUnit.MILLISECONDS.toHours(diffLastReload);
//        long diffLastReloadInDays = TimeUnit.MILLISECONDS.toDays(diffLastReload);
//        if (shared.getLong("last_reload_time", 0) == 0) {
//            txtLastReload.setText("تا کنون بروزرسانی نکرده اید");
//        } else if (diffLastReloadInHours > 23) {
//            txtLastReload.setText(diffLastReloadInDays + " روز پیش");
//        } else if (diffLastReloadInMinutes > 59) {
//            txtLastReload.setText(diffLastReloadInHours + " ساعت پیش");
//        } else if (diffLastReloadInMinutes > 0) {
//            txtLastReload.setText(diffLastReloadInMinutes + " دقیقه پیش");
//        } else if (diffLastReloadInMinutes == 0) {
//            txtLastReload.setText("هم اکنون");
//        }
//        TextView cFollowers = (TextView) findViewById(R.id.cFollowers);
//        TextView cFollowings = (TextView) findViewById(R.id.cFollowings);
//        TextView cUnFollowers = (TextView) findViewById(R.id.cUnFollowers);
//        TextView cFans = (TextView) findViewById(R.id.cFans);
//        TextView cMutual = (TextView) findViewById(R.id.cMutual);
//        TextView cNewFollowers = (TextView) findViewById(R.id.cNewFollowers);
//        TextView cNewUnFollowers = (TextView) findViewById(R.id.cNewUnFollowers);
//        cFollowers.setText(String.valueOf(db.rawQuery("SELECT * FROM followers", null).getCount()));
//        cFollowings.setText(String.valueOf(db.rawQuery("SELECT * FROM followings", null).getCount()));
//        cUnFollowers.setText(String.valueOf(db.rawQuery("SELECT * FROM followings WHERE username NOT IN(SELECT username FROM followers WHERE username IS NOT NULL)", null).getCount()));
//        cFans.setText(String.valueOf(db.rawQuery("SELECT * FROM followers WHERE username NOT IN(SELECT username FROM followings WHERE username IS NOT NULL)", null).getCount()));
//        cMutual.setText(String.valueOf(db.rawQuery("SELECT * FROM followings INNER JOIN followers ON followings.username = followers.username", null).getCount()));
//        cNewFollowers.setText(String.valueOf(db.rawQuery("SELECT * FROM followers WHERE username NOT IN(SELECT username FROM first_followers WHERE username IS NOT NULL)", null).getCount()));
//        cNewUnFollowers.setText(String.valueOf(db.rawQuery("SELECT * FROM first_followers WHERE username NOT IN(SELECT username FROM followers WHERE username IS NOT NULL)", null).getCount()));
//        TextView txtNewFollowersD = (TextView) findViewById(R.id.txtNewFollowersD);
//        TextView txtNewUnFollowersD = (TextView) findViewById(R.id.txtNewUnFollowersD);
//        TextView txtBlockedYouD = (TextView) findViewById(R.id.txtBlockedYouD);
//        TextView txtBlockedByYouD = (TextView) findViewById(R.id.txtBlockedByYouD);
//        long diffFirstReload = System.currentTimeMillis() - shared.getLong("first_reload_time", 0);
//        long diffFirstReloadInHours = TimeUnit.MILLISECONDS.toHours(diffFirstReload);
//        long diffFirstReloadInDays = TimeUnit.MILLISECONDS.toDays(diffFirstReload);
//        if (shared.getLong("first_reload_time", 0) == 0) {
//
//        } else if (diffFirstReloadInDays > 0) {
//            txtNewFollowersD.setText("افرادی که در " + diffFirstReloadInDays + " روز گذشته شما را فالو کرده اند .");
//            txtNewUnFollowersD.setText("افرادی که در " + diffFirstReloadInDays + " روز گذشته شما را آنفالو کرده اند .");
//            txtBlockedYouD.setText("افرادی که در " + diffFirstReloadInDays + " روز گذشته شما را بلاک کرده اند .");
//            txtBlockedByYouD.setText("افرادی که در " + diffFirstReloadInDays + " روز گذشته آنها را بلاک کرده اید .");
//        } else if (diffFirstReloadInHours > 0) {
//            txtNewFollowersD.setText("افرادی که در " + diffFirstReloadInHours + " ساعت گذشته شما را فالو کرده اند .");
//            txtNewUnFollowersD.setText("افرادی که در " + diffFirstReloadInHours + " ساعت گذشته شما را آنفالو کرده اند .");
//            txtBlockedYouD.setText("افرادی که در " + diffFirstReloadInHours + " ساعت گذشته شما را بلاک کرده اند .");
//            txtBlockedByYouD.setText("افرادی که در " + diffFirstReloadInHours + " ساعت گذشته آنها را بلاک کرده اید .");
//        }
//    }
    }

    @Override
    public void selectToChange(String userName, String pass) {
        InstagramAutenticationDialog dialog = new InstagramAutenticationDialog(true,userName,pass);
        dialog.setCancelable(true);
        dialog.show(getChildFragmentManager(), ":");
    }
}

