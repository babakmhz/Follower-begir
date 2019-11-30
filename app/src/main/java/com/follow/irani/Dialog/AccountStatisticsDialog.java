package com.follow.irani.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.follow.irani.App;
import com.follow.irani.Manager.DataBaseHelper;
import com.follow.irani.R;
import com.follow.irani.data.InstagramMedia;
import com.follow.irani.databinding.DialogAccountStatisticsBinding;
import com.follow.irani.instaAPI.InstaApiException;
import com.follow.irani.instaAPI.InstagramApi;
import com.follow.irani.parser.MediasParser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

@SuppressLint("ValidFragment")
public class AccountStatisticsDialog extends DialogFragment {

    private final String picURl;
    private InstagramApi api = InstagramApi.getInstance();
    private DataBaseHelper dbHeplper;
    private SQLiteDatabase db;
    private SharedPreferences shared;
    private SharedPreferences.Editor editor;
    private boolean is_more_available = false;
    private DialogAccountStatisticsBinding binding;
    private ProgressDialog progressDoalog;
    private String next_max_id;

    public AccountStatisticsDialog(String profilePicURL) {
        this.picURl = profilePicURL;
    }


    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(App.currentActivity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(App.currentActivity), R.layout.dialog_account_statistics, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        binding.prg.setVisibility(View.VISIBLE);
        binding.tvUserName.setText(App.user.getUserName());

//        App.currentActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                App.ProgressDialog(App.currentActivity, "در حال دریافت پست ها، لطفا صبور باشید ...");
//
//            }
//        });


        //endregion
        shared = App.currentActivity.getSharedPreferences("UserPrefs", MODE_PRIVATE);
        editor = shared.edit();
        dbHeplper = new DataBaseHelper(App.context);
        try {
            dbHeplper.createDatabase();
            dbHeplper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
            binding.prg.setVisibility(View.GONE);

        }
        db = dbHeplper.getWritableDatabase();
        Picasso.get().load(App.profilePicURl).into(binding.profileImage);
        binding.tvUserName.setText(App.user.getUserName());
        binding.tvLikeCoinCount.setText(App.likeCoin + "");
        binding.tvFollowCoinCount.setText(App.followCoin + "");
        binding.imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        reloadPosts();
        return dialog;
    }

    private void reloadPosts() {

        ConnectivityManager cm = (ConnectivityManager) App.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            App.Toast(App.currentActivity, "خطای عدم اتصال! دستگاه خود را به اینترنت متصل کنید.");
            binding.prg.setVisibility(View.GONE);

        } else {

            db.execSQL("DELETE FROM posts");
            getPosts();
        }
    }

    private void getPosts() {
        try {
            api.GetSelfFeed(new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    MediasParser parser = new MediasParser();
                    ArrayList<InstagramMedia> postsList = parser.parseMedias(response);
                    for (int i = 0; i < postsList.size(); i++) {
                        InstagramMedia post = postsList.get(i);
                        db.execSQL("INSERT or replace INTO posts (postid, url, comment_count, like_count) " + "VALUES('"
                                + post.getPhotoId() + "','"
                                + post.getImageUrl() + "','"
                                + post.getCommentsCount() + "','"
                                + post.getLikesCount() + "')");
                    }
                    is_more_available = false;
                    try {
                        is_more_available = response.getBoolean("more_available");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        is_more_available = false;
                    }
                    if (is_more_available)
                        fetchNextUserMedias();
                    else
                        fetchUserMediasFinish();
                }

                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                    binding.prg.setVisibility(View.GONE);
                    App.CancelProgressDialog();

                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }

    private void fetchNextUserMedias() {
        Cursor cursor = db.rawQuery("select * from posts", null);
        cursor.moveToLast();
        try {
            api.GetSelfFeed(cursor.getString(cursor.getColumnIndex("postid")),
                    new InstagramApi.ResponseHandler() {
                        @Override
                        public void OnSuccess(JSONObject response) {
                            MediasParser parser = new MediasParser();
                            ArrayList<InstagramMedia> postsList = parser.parseMedias(response);
                            for (int i = 0; i < postsList.size(); i++) {
                                InstagramMedia post = postsList.get(i);
                                db.execSQL("INSERT or replace INTO posts (postid, url, comment_count, like_count) " + "VALUES('"
                                        + post.getPhotoId() + "','"
                                        + post.getImageUrl() + "','"
                                        + post.getCommentsCount() + "','"
                                        + post.getLikesCount() + "')");
                            }
                            is_more_available = false;
                            try {
                                is_more_available = response.getBoolean("more_available");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                is_more_available = false;
                            }
                            if (is_more_available)
                                fetchNextUserMedias();
                            else
                                fetchUserMediasFinish();
                        }

                        @Override
                        public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                            binding.prg.setVisibility(View.GONE);
                            App.CancelProgressDialog();
                        }
                    });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }

    private void fetchUserMediasFinish() {
        App.CancelProgressDialog();
        binding.prg.setVisibility(View.GONE);
        setCounts();
        if (!shared.getBoolean("is_get_posts", false)) {
            editor.putBoolean("is_get_posts", true);
            editor.apply();
        }
        fetchUserFollowersStatics();
    }

    private void fetchUserFollowersStatics() {
        binding.tvNotFollowedYou.setText(String.valueOf(db.rawQuery("SELECT * FROM followings WHERE username NOT IN(SELECT username FROM followers WHERE username IS NOT NULL)", null).getCount()));
        binding.tvNotFollowedByYou.setText(String.valueOf(db.rawQuery("SELECT * FROM followers WHERE username NOT IN(SELECT username FROM followings WHERE username IS NOT NULL)", null).getCount()));
        populateMostLikedFromDatabase();
        populateLeastLikedFromDatabase();
        populateMostCommentsFromDatabase();
        populatLeastCommentsFromDatabase();

    }

    private void setCounts() {
        try {

            Cursor like_count = db.rawQuery("SELECT SUM(like_count) FROM posts", null);
            if (like_count.moveToFirst()) {
                binding.tvLikeCounts.setText(String.valueOf(like_count.getInt(0)));
                if (db.rawQuery("SELECT * FROM posts", null).getCount() == 0) {
                    binding.tvAverageLike.setText(0 + "");
                } else {
                    Float num = Float.valueOf((like_count.getInt(0)) / (db.rawQuery("SELECT * FROM posts", null).getCount()));
                    binding.tvAverageLike.setText(num + "");
                }
            }
            Cursor comment_count = db.rawQuery("SELECT SUM(comment_count) FROM posts", null);
            if (comment_count.moveToFirst()) {
                binding.tvCommentCounts.setText(String.valueOf(comment_count.getInt(0)));
                if (db.rawQuery("SELECT * FROM posts", null).getCount() == 0) {
                    binding.tvAverageComment.setText(0 + "");
                } else {
                    Float num = Float.valueOf((comment_count.getInt(0)) / (db.rawQuery("SELECT * FROM posts", null).getCount()));
                    binding.tvAverageComment.setText(num + "");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //binding.tvCommentCounts.setText(String.valueOf(db.rawQuery("SELECT * FROM posts", null).getCount()));

    }

    private void populateMostLikedFromDatabase() {
        try {
            dbHeplper.createDatabase();
            dbHeplper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            new AsyncTask<Void, Void, Void>() {
                String urlll;
                String likeCount;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    binding.tvMostLikedCount.setText(likeCount);
                    Picasso.get().load(urlll).into(binding.imvMostLiked);
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    Cursor cursor = db.rawQuery("SELECT * FROM posts ORDER BY like_count DESC LIMIT 10", null);
                    cursor.moveToFirst();
                    urlll = cursor.getString(cursor.getColumnIndex("url"));
                    likeCount = String.valueOf(cursor.getInt(cursor.getColumnIndex("like_count")));
                    cursor.close();

                    return null;
                }
            }.execute();



        } catch (SQLiteException ignored) {
        }
    }

    private void populateLeastLikedFromDatabase() {
        try {
            dbHeplper.createDatabase();
            dbHeplper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {

            new AsyncTask<Void, Void, Void>() {
                String urlll;
                String likeCount;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    binding.tvLeastLiked.setText(likeCount);
                    Picasso.get().load(urlll).into(binding.imvLeastLiked);
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    Cursor cursor = db.rawQuery("SELECT * FROM posts ORDER BY like_count Asc LIMIT 10", null);
                    cursor.moveToFirst();
                    urlll = cursor.getString(cursor.getColumnIndex("url"));
                    likeCount = String.valueOf(cursor.getInt(cursor.getColumnIndex("like_count")));
                    cursor.close();

                    return null;
                }
            }.execute();

        } catch (SQLiteException ignored) {
        }
    }

    private void populateMostCommentsFromDatabase() {

        try {
            new AsyncTask<Void, Void, Void>() {
                String urlll;
                String likeCount;

                @Override
                protected void onPreExecute() {
                    try {
                        dbHeplper.createDatabase();
                        dbHeplper.openDatabase();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    binding.tvMostComment.setText(likeCount);
                    Picasso.get().load(urlll).into(binding.imvMostComment);
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    Cursor cursor = db.rawQuery("SELECT * FROM posts ORDER BY comment_count DESC LIMIT 10", null);
                    cursor.moveToFirst();
                    urlll = cursor.getString(cursor.getColumnIndex("url"));
                    likeCount = String.valueOf(cursor.getInt(cursor.getColumnIndex("comment_count")));
                    cursor.close();

                    return null;
                }
            }.execute();


        } catch (SQLiteException ignored) {
        }
    }

    private void populatLeastCommentsFromDatabase() {
        try {
            dbHeplper.createDatabase();
            dbHeplper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {

            new AsyncTask<Void, Void, Void>() {
                String urlll;
                String likeCount;

                @Override
                protected void onPreExecute() {
                    try {
                        dbHeplper.createDatabase();
                        dbHeplper.openDatabase();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    binding.tvLeastComment.setText(likeCount);
                    Picasso.get().load(urlll).into(binding.imvLeastComment);
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    Cursor cursor = db.rawQuery("SELECT * FROM posts ORDER BY comment_count Asc LIMIT 10", null);
                    cursor.moveToFirst();
                    urlll = cursor.getString(cursor.getColumnIndex("url"));
                    likeCount = String.valueOf(cursor.getInt(cursor.getColumnIndex("comment_count")));
                    cursor.close();

                    return null;
                }
            }.execute();



        } catch (SQLiteException ignored) {
        }
        binding.prg.setVisibility(View.VISIBLE);
        try {
            reloadData();

        } catch (Exception e) {

        }
    }


    private void reloadData() {
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

    private void fetchFollowers() {
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
                    reloadFail();
                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }

    private void fetchNextFollowers() {
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
                        reloadFail();
                        binding.prg.setVisibility(View.GONE);
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
                    reloadFail();
                    binding.prg.setVisibility(View.GONE);
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
            try {
                progressDoalog.dismiss();
                binding.prg.setVisibility(View.GONE);
            } catch (Exception e) {

            }

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
                        reloadFail();
                        binding.prg.setVisibility(View.GONE);
                    }
                }

                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                    reloadFail();
                    binding.prg.setVisibility(View.GONE);
                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }

    public void reloadFail() {
        binding.prg.setVisibility(View.GONE);
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
}
