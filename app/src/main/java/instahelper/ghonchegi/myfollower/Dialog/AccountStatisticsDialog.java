package instahelper.ghonchegi.myfollower.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Manager.DataBaseHelper;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.data.InstagramMedia;
import instahelper.ghonchegi.myfollower.databinding.DialogAccountStatisticsBinding;
import instahelper.ghonchegi.myfollower.instaAPI.InstaApiException;
import instahelper.ghonchegi.myfollower.instaAPI.InstagramApi;
import instahelper.ghonchegi.myfollower.parser.MediasParser;

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

    public AccountStatisticsDialog(String profilePicURL) {
        this.picURl = profilePicURL;
    }


    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_account_statistics, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        binding.prg.setVisibility(View.VISIBLE);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                App.ProgressDialog(getActivity(), "در حال دریافت پست ها، لطفا صبور باشید ...");

            }
        });
        //endregion
        shared = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
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
        Picasso.get().load(picURl).into(binding.imgProfileImage);
        binding.imvArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        reloadPosts();
        return dialog;
    }

    public void reloadPosts() {

        ConnectivityManager cm = (ConnectivityManager) App.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            App.Toast(getContext(), "خطای عدم اتصال! دستگاه خود را به اینترنت متصل کنید.");
            binding.prg.setVisibility(View.GONE);

        } else {

            db.execSQL("DELETE FROM posts");
            getPosts();
        }
    }

    public void getPosts() {
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


    public void setCounts() {
        Cursor like_count = db.rawQuery("SELECT SUM(like_count) FROM posts", null);
        if (like_count.moveToFirst()) {
            binding.tvLikeCounts.setText(String.valueOf(like_count.getInt(0)));
            Float num = Float.valueOf((like_count.getInt(0)) / (db.rawQuery("SELECT * FROM posts", null).getCount()));
            binding.tvAverageLike.setText(num + "");
        }
        Cursor comment_count = db.rawQuery("SELECT SUM(comment_count) FROM posts", null);
        if (comment_count.moveToFirst()) {
            binding.tvCommentCounts.setText(String.valueOf(comment_count.getInt(0)));
            Float num = Float.valueOf((comment_count.getInt(0)) / (db.rawQuery("SELECT * FROM posts", null).getCount()));
            binding.tvAverageComment.setText(num + "");

        }
        //binding.tvCommentCounts.setText(String.valueOf(db.rawQuery("SELECT * FROM posts", null).getCount()));

    }

    public void populateMostLikedFromDatabase() {
        try {
            dbHeplper.createDatabase();
            dbHeplper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM posts ORDER BY like_count DESC LIMIT 10", null);
            cursor.moveToFirst();
            binding.tvMostLikedCount.setText(cursor.getInt(cursor.getColumnIndex("like_count")) + "");
            Picasso.get().load(cursor.getString(cursor.getColumnIndex("url"))).into(binding.imvMostLiked);

            cursor.close();
        } catch (SQLiteException ignored) {
        }
    }

    public void populateLeastLikedFromDatabase() {
        try {
            dbHeplper.createDatabase();
            dbHeplper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM posts ORDER BY like_count Asc LIMIT 10", null);
            cursor.moveToFirst();
            binding.tvLeastLiked.setText(cursor.getInt(cursor.getColumnIndex("like_count")) + "");
            Picasso.get().load(cursor.getString(cursor.getColumnIndex("url"))).into(binding.imvLeastLiked);

            cursor.close();
        } catch (SQLiteException ignored) {
        }
    }

    public void populateMostCommentsFromDatabase() {
        try {
            dbHeplper.createDatabase();
            dbHeplper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM posts ORDER BY comment_count DESC LIMIT 10", null);
            cursor.moveToFirst();
            binding.tvMostComment.setText(cursor.getInt(cursor.getColumnIndex("comment_count")) + "");
            Picasso.get().load(cursor.getString(cursor.getColumnIndex("url"))).into(binding.imvMostComment);

            cursor.close();
        } catch (SQLiteException ignored) {
        }
    }

    public void populatLeastCommentsFromDatabase() {
        try {
            dbHeplper.createDatabase();
            dbHeplper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM posts ORDER BY comment_count Asc LIMIT 10", null);
            cursor.moveToFirst();
            binding.tvLeastComment.setText(cursor.getInt(cursor.getColumnIndex("comment_count")) + "");
            Picasso.get().load(cursor.getString(cursor.getColumnIndex("url"))).into(binding.imvLeastComment);

            cursor.close();
        } catch (SQLiteException ignored) {
        }
    }
}
