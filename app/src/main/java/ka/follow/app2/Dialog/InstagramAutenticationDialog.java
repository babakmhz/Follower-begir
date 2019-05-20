package ka.follow.app2.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import ka.follow.app2.Activities.MainActivity;
import ka.follow.app2.App;
import ka.follow.app2.Manager.DataBaseHelper;
import ka.follow.app2.R;
import ka.follow.app2.databinding.DialogAuthenticateBinding;
import ka.follow.app2.instaAPI.InstaApiException;
import ka.follow.app2.instaAPI.InstagramApi;

import static android.content.Context.MODE_PRIVATE;
import static ka.follow.app2.App.TAG;

@SuppressLint("ValidFragment")
public class InstagramAutenticationDialog extends DialogFragment {

    DialogAuthenticateBinding binding;
    private InstagramApi api = InstagramApi.getInstance();
    private Handler handler;
    private WebView loginWebView;
    private SharedPreferences shared;
    private SharedPreferences.Editor editor;
    private SQLiteDatabase db;
    private DataBaseHelper dbHeplper;
    private boolean isRedirectd = false;
    private String userName, password;

    public InstagramAutenticationDialog(boolean isRedirect, @Nullable String userName, @Nullable String password) {
        this.isRedirectd = isRedirect;
        this.password = password;
        this.userName = userName;

    }


    private static void clearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setContentView(R.layout.dialog_authenticate);
        dbHeplper = new DataBaseHelper(getContext());
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_authenticate, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //endregion
        handler = new Handler(getActivity().getMainLooper());
        loginWebView = binding.webViewAuthenticate;
        LoginWebClient client = new LoginWebClient();
        shared = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        editor = shared.edit();
        loginWebView.getSettings().setJavaScriptEnabled(true);
        loginWebView.addJavascriptInterface(new MyJavaScriptInterface(), "MYOBJECT");
        loginWebView.setWebViewClient(client);
        loginWebView.clearCache(true);
        loginWebView.clearHistory();
        loginWebView.getSettings().setSaveFormData(false);
        clearCookies(App.currentActivity);
        if (isRedirectd) {
            logOut();
            OnCredentialsEntered(userName, password);
        } else
            loginWebView.loadUrl("https://www.instagram.com/accounts/login/?force_classic_login");
        db = dbHeplper.getWritableDatabase();


        return dialog;
    }

    private void OnCredentialsEntered(String username, String password) {
        binding.prg.setVisibility(View.VISIBLE);
        api = InstagramApi.getInstance();
        api.Login(username, password, new InstagramApi.ResponseHandler() {
            @Override
            public void OnSuccess(JSONObject response) {
                Log.i(TAG, "OnSuccess: " + response);
                try {
                    JSONObject jsonRootObject = new JSONObject(String.valueOf(response));
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
                binding.prg.setVisibility(View.GONE);
                try {
                    switch (errorResponse.getString("error_type")) {
                        case "checkpoint_challenge_required":
                            AccountNeedsVerification dialog = new AccountNeedsVerification();
                            dialog.show(getChildFragmentManager(), "");
                            break;
                        case "bad_password":
                            Toast.makeText(getActivity(), "نام کاربری یا رمز عبور اشتباه ست", Toast.LENGTH_LONG).show();
                            break;
                        case "invalid_user":
                            Toast.makeText(getActivity(), "چنین کاربری وجود ندارد", Toast.LENGTH_LONG).show();
                            break;
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
                    App.userId=null;
                    App.profilePicURl=null;


                }

                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {

                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }

    private class LoginWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("instagram.com/accounts/login/?force_classic_login")) {
                view.loadUrl(url);
            } else if (url.contains("password/reset")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                getActivity().startActivity(intent);
            }

            else if (url.contains("challenge"))
                view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.equalsIgnoreCase("https://www.instagram.com/")) {
                view.stopLoading();
                clearCookies(getActivity());
                return;
            }
            binding.prg.setVisibility(View.VISIBLE);

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            StringBuilder sb = new StringBuilder();
            sb.append("var values = { user:'' , pass:'' }; ");
            sb.append("document.getElementsByTagName('form')[0].onsubmit = function () {");
            sb.append("var objPWD, objAccount;var str = '';");
            sb.append("var inputs = document.getElementsByTagName('input');");
            sb.append("for (var i = 0; i < inputs.length; i++) {");
            sb.append("if (inputs[i].name.toLowerCase() === 'password') {objPWD = inputs[i];}");
            sb.append("else if (inputs[i].name.toLowerCase() === 'username') {objAccount = inputs[i];}");
            sb.append("}");
            sb.append("if (objAccount != null) " +
                    "{values.user = objAccount.value;" +
                    "}");
            sb.append("if (objPWD != null) { values.pass = objPWD.value;}");
            sb.append("window.MYOBJECT.processHTML(JSON.stringify(values));");
            sb.append("return true;");
            sb.append("};");
            view.loadUrl("javascript:" + sb.toString());
            try {
                if (!url.equals("https://www.instagram.com/")) {
                    binding.prg.setVisibility(View.GONE);
                }
            } catch (Exception ignored) {

            }
        }
    }

    private class MyJavaScriptInterface {
        @JavascriptInterface
        public void processHTML(String json) {
            String user = null;
            String password = null;
            try {
                JSONObject jsonObject = new JSONObject(json);
                user = jsonObject.getString("user");
                password = jsonObject.getString("pass");
            } catch (JSONException ignored) {

            }
            final String username = user;
            final String password_final = password;
            handler.post(new Runnable() {
                @Override
                public void run() {


                    OnCredentialsEntered(username, password_final);
                }
            });
        }
    }


}
