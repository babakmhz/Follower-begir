package instahelper.ghonchegi.myfollower.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
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

import instahelper.ghonchegi.myfollower.Activities.MainActivity;
import instahelper.ghonchegi.myfollower.Manager.DataBaseHelper;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.DialogAuthenticateBinding;
import instahelper.ghonchegi.myfollower.instaAPI.InstagramApi;

import static android.content.Context.MODE_PRIVATE;

public class InstagramAutenticationDialog extends DialogFragment {

    DialogAuthenticateBinding binding;
    private InstagramApi api;
    private Handler handler;
    private WebView loginWebView;
    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    public static void clearCookies(Context context) {
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
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_authenticate, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //endregion
        handler = new Handler(getActivity().getMainLooper());
        loginWebView = binding.webViewAuthenticate;
        LoginWebClient client = new LoginWebClient();
        shared = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        loginWebView.getSettings().setJavaScriptEnabled(true);
        loginWebView.addJavascriptInterface(new MyJavaScriptInterface(), "MYOBJECT");
        loginWebView.setWebViewClient(client);
        loginWebView.clearCache(true);
        loginWebView.clearHistory();
        loginWebView.getSettings().setSaveFormData(false);
        clearCookies(getContext());
        loginWebView.loadUrl("https://www.instagram.com/accounts/login/?force_classic_login");


        return dialog;
    }

    private void OnCredentialsEntered(String username, String password) {
        binding.prg.setVisibility(View.VISIBLE);
        api = InstagramApi.getInstance();
        api.Login(username, password, new InstagramApi.ResponseHandler() {
            @Override
            public void OnSuccess(JSONObject response) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
                dismiss();

            }

            @Override
            public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                binding.prg.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "نام کاربری یا رمز عبور اشتباه ست", Toast.LENGTH_LONG).show();
//                try {
//                    Toast.makeText(getActivity(), "نام کاربری یا رمز عبور اشتباه ست", Toast.LENGTH_LONG).show();
//                    String errorMessage = "";
//                    try {
//                        errorMessage = errorResponse.getString("message");
//                    } catch (NullPointerException e) {
//                        e.printStackTrace();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    private class LoginWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("instagram.com/accounts/login/?force_classic_login")) {
                view.loadUrl(url);
            } else if (url.contains("instagram.com/accounts/password/reset")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                getActivity().startActivity(intent);
            }
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
