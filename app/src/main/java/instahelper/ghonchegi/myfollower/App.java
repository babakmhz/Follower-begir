package instahelper.ghonchegi.myfollower;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import instahelper.ghonchegi.myfollower.Manager.Config;
import instahelper.ghonchegi.myfollower.Manager.VolleyManager;
import instahelper.ghonchegi.myfollower.data.InstagramUser;
import ir.tapsell.sdk.Tapsell;

public class App extends Application {

    public static final String TAG = "MyFollower";
    public static final String TapSellKey = "mfiqilecflqlrncrqjcqerkadkaepgoaamqknltdibneensaoagqqjaolitflpafjaftqd";
    public static final String TapSelZoneId = "5c8684a78e2c780001f03fc3";
    public static String Base_URL = "http://masoudzarjani.ir/api/v1/";
    public static Activity currentActivity;
    public static LayoutInflater inflater;
    public static Dialog progressDialog;
    public static SharedPreferences preferences;
    public static RequestQueue requestQueue;
    public static Context context;
    public static String UUID = null;
    public static String Api_Token = null;
    public static int followCoin;
    public static int likeCoin;
    public static String profilePicURl;
    public static boolean isAdAvailable = false;
    public static boolean isPrivateAccount = true;
    public static String userId = null;
    public static InstagramUser user;
    public static String responseBanner;
    public static String SkuSpecialWheel = "Item1";

    public static void Toast(Context context, String message) {
        View view = App.inflater.inflate(R.layout.toast, null);
        ViewGroup layoutToast = (ViewGroup) view.findViewById(R.id.toast_layout_root);
        TextView txtToastMessage = (TextView) layoutToast.findViewById(R.id.txtToastMessage);
        txtToastMessage.setText(message);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 24);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layoutToast);
        toast.show();
    }

    public static void ProgressDialog(Context context, String progressMessage) {
        progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.dialog_progress);
        TextView txtProgressMessage = (TextView) progressDialog.findViewById(R.id.txtProgressMessage);
        if (progressMessage != null) {
            txtProgressMessage.setText(progressMessage);
        }
        progressDialog.show();
    }

    public static void CancelProgressDialog() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
//        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        context = getApplicationContext();

        //   Pushe.initialize(this, false);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        requestQueue = VolleyManager.getRequestQueue(getBaseContext());
        requestQueue.start();
        Tapsell.initialize(this, TapSellKey);
        Config.setupShopItems();

        //FontsOverride.setDefaultFont(this, "MONOSPACE", "iran.ttf");
    }

}
