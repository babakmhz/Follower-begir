package ir.novahar.followerbegir.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import ir.novahar.followerbegir.App;
import ir.novahar.followerbegir.Manager.DataBaseHelper;
import ir.novahar.followerbegir.R;
import ir.novahar.followerbegir.instaAPI.InstagramApi;


public class ActivityLoginWebview extends AppCompatActivity {

    ProgressBar prg;
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

    @Override
    protected void onResume() {
        super.onResume();
        this.overridePendingTransition(0, 0);
        App.currentActivity = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_authenticate);
        handler = new Handler(this.getMainLooper());
        loginWebView = (WebView) findViewById(R.id.webViewAuthenticate);
        LoginWebClient client = new LoginWebClient();
        prg = findViewById(R.id.prg);
        shared = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        loginWebView.getSettings().setJavaScriptEnabled(true);
        loginWebView.addJavascriptInterface(new MyJavaScriptInterface(), "MYOBJECT");
        loginWebView.setWebViewClient(client);
        loginWebView.clearCache(true);
        loginWebView.clearHistory();
        loginWebView.getSettings().setSaveFormData(false);
        clearCookies(this);
        loginWebView.loadUrl("https://www.instagram.com/accounts/login/?force_classic_login");
    }

    private void OnCredentialsEntered(String username, String password) {
        prg.setVisibility(View.VISIBLE);
        api = InstagramApi.getInstance();
        api.Login(username, password, new InstagramApi.ResponseHandler() {
            @Override
            public void OnSuccess(JSONObject response) {
                Intent intent = new Intent(ActivityLoginWebview.this, MainActivity.class);
                ActivityLoginWebview.this.startActivity(intent);
                finish();
            }

            @Override
            public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                prg.setVisibility(View.GONE);
                try {
                    String errorMessage = "";
                    try {
                        errorMessage = errorResponse.getString("message");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                ActivityLoginWebview.this.startActivity(intent);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.equalsIgnoreCase("https://www.instagram.com/")) {
                view.stopLoading();
                clearCookies(ActivityLoginWebview.this);
                return;
            }
            prg.setVisibility(View.VISIBLE);
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
                if (!url.equals("https://www.instagram.com/"))
                    prg.setVisibility(View.GONE);
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
                    DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext());
                    

                    OnCredentialsEntered(username, password_final);
                }
            });
        }
    }


}