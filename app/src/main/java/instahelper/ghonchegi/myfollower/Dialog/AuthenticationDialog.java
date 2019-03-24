package instahelper.ghonchegi.myfollower.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import instahelper.ghonchegi.myfollower.Activities.MainActivity;
import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Manager.DataBaseHelper;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.InstaBinding;
import instahelper.ghonchegi.myfollower.instaAPI.InstaApiException;
import instahelper.ghonchegi.myfollower.instaAPI.InstagramApi;

import static android.content.Context.MODE_PRIVATE;
import static instahelper.ghonchegi.myfollower.App.TAG;
import static instahelper.ghonchegi.myfollower.App.user;

@SuppressLint("ValidFragment")
public class AuthenticationDialog extends DialogFragment {

    private final String userName;
    private final String password;
    InstaBinding binding;
    private InstagramApi api = InstagramApi.getInstance();
    private SharedPreferences shared;
    private SharedPreferences.Editor editor;
    private SQLiteDatabase db;
    private DataBaseHelper dbHeplper;
    private boolean isRedirectd = false;

    public AuthenticationDialog(boolean isRedirect, @Nullable String userName, @Nullable String password) {
        this.isRedirectd = isRedirect;
        this.password = password;
        this.userName = userName;

    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dbHeplper = new DataBaseHelper(getContext());
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.insta, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //endregion
        shared = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        editor = shared.edit();
        init();

        if (isRedirectd) {
            logOut();
            binding.editText.setText(userName);
            binding.editText2.setText(password);
            OnCredentialsEntered(userName, password);
        }
        db = dbHeplper.getWritableDatabase();
        return dialog;
    }

    private void init() {
        binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(binding.editText.getText().toString()) && !TextUtils.isEmpty(binding.editText2.getText().toString())) {
                    binding.button.setBackground(getResources().getDrawable(R.drawable.insta_btn_back_active));
                } else
                    binding.button.setBackground(getResources().getDrawable(R.drawable.insta_btn_back));


            }
        });
        binding.editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(binding.editText.getText().toString()) && !TextUtils.isEmpty(binding.editText2.getText().toString())) {
                    binding.button.setBackground(getResources().getDrawable(R.drawable.insta_btn_back_active));
                } else
                    binding.button.setBackground(getResources().getDrawable(R.drawable.insta_btn_back));


            }
        });

        binding.button.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.editText.getText().toString()) && !TextUtils.isEmpty(binding.editText2.getText().toString())) {
                OnCredentialsEntered(binding.editText.getText().toString(), binding.editText2.getText().toString());
            }

        });
    }

    private void OnCredentialsEntered(String username, String password) {
        binding.prgInstagram.setVisibility(View.VISIBLE);
        binding.tvLogin.setVisibility(View.GONE);
        api = InstagramApi.getInstance();
        api.Login(username, password, new InstagramApi.ResponseHandler() {
            @Override
            public void OnSuccess(JSONObject response) {
                Log.i(TAG, "OnSuccess: " + response);
                binding.prgInstagram.setVisibility(View.GONE);
                binding.tvLogin.setVisibility(View.VISIBLE);
                JSONObject jsonRootObject = null;
                try {
                    jsonRootObject = new JSONObject(String.valueOf(response));
                    JSONObject newObj = jsonRootObject.getJSONObject("logged_in_user");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
                dismiss();
                getActivity().finish();

            }

            @Override
            public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                binding.prgInstagram.setVisibility(View.GONE);
                binding.tvLogin.setVisibility(View.VISIBLE);
                Log.i(TAG, "onFailed: " + errorResponse);

                try {
                    if (errorResponse.getString("error_type").equals("checkpoint_challenge_required")) {
                        Toast.makeText(getActivity(), "حساب کاربری شما نیاز به تایید دارد.ابتدا با نرم افزار اینستاگرام وارد حساب خود شوید", Toast.LENGTH_LONG).show();
                    } else if (errorResponse.getString("error_type").equals("bad_password")) {
                        Toast.makeText(getActivity(), "نام کاربری یا رمز عبور اشتباه ست", Toast.LENGTH_LONG).show();
                    }else if (errorResponse.getString("error_type").equals("invalid_user")) {
                        Toast.makeText(getActivity(), "چنین کاربری وجود ندارد", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void logOut() {
        try {
            api.Logout(new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    db.execSQL("DELETE FROM posts");
                    db.execSQL("DELETE FROM followers");
                    db.execSQL("DELETE FROM followings");
                    db.execSQL("DELETE FROM first_followers");
                    db.execSQL("DELETE FROM first_followings");
                    editor.putString("username", "");
                    editor.putString("profile_pic_url", "");
                    editor.putString("full_name", "");
                    editor.putBoolean("is_first_reload", true);
                    editor.putBoolean("is_get_posts", false);
                    editor.putLong("first_reload_time", 0);
                    editor.putLong("last_reload_time", 0);
                    editor.putBoolean("auto_unfollow_is_active", false);
                    editor.apply();

                    App.followCoin = 0;
                    App.Api_Token = null;
                    App.UUID = null;
                    App.likeCoin = 0;
                    App.userId = null;
                    App.profilePicURl = null;


                }

                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {

                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }


}
