package instahelper.ghonchegi.myfollower.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Fragments.GetCoin.GetCoinFragment;
import instahelper.ghonchegi.myfollower.Fragments.HomeFragment;
import instahelper.ghonchegi.myfollower.Fragments.Offers.ShopFragment;
import instahelper.ghonchegi.myfollower.Fragments.Purchase.PurchaseFragment;
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.Models.Offers;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.ActivityMainBinding;
import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAd;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellRewardListener;
import ir.tapsell.sdk.TapsellShowOptions;

import static instahelper.ghonchegi.myfollower.App.Base_URL;
import static instahelper.ghonchegi.myfollower.App.requestQueue;

public class MainActivity extends AppCompatActivity {
    public static TapsellAd ad;
    public static ProgressDialog progressDialog;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private HomeFragment homeFragment = new HomeFragment();
    private PurchaseFragment purchaseFragment = new PurchaseFragment();
    private GetCoinFragment getCoinFragment = new GetCoinFragment();
    private ShopFragment shopFragment=new ShopFragment();
    private int currentItemId;
    private ActivityMainBinding binding;

    public static void globalLoadAd(Context context, final String zoneId, final int catchType) {
        if (MainActivity.ad == null) {
            TapsellAdRequestOptions options = new TapsellAdRequestOptions(catchType);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("بارگزاری ...");
            progressDialog.show();
            Tapsell.requestAd(context, zoneId, options, new TapsellAdRequestListener() {
                @Override
                public void onError(String error) {
                    // Toast.makeText(MainActivity.this, "ERROR:\n" + error, Toast.LENGTH_SHORT).show();
                    Log.e("Tapsell", "ERROR:" + error);
                    progressDialog.dismiss();
                }

                @Override
                public void onAdAvailable(TapsellAd ad) {

                    MainActivity.ad = ad;
                    App.isAdAvailable = true;
                    Log.e("Tapsell", "adId: " + (ad == null ? "NULL" : ad.getId()) + " available, zoneId: " + (ad == null ? "NULL" : ad.getZoneId()));
                    progressDialog.dismiss();
//                new AlertDialog.Builder(MainActivity.this).setTitle("Title").setMessage("Message").show();
                }

                @Override
                public void onNoAdAvailable() {
                    App.isAdAvailable = false;
                    //  Toast.makeText(MainActivity.this, "No Ad Available", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    Log.e("Tapsell", "No Ad Available");
                }

                @Override
                public void onNoNetwork() {
                    App.isAdAvailable = false;
                    //Toast.makeText(MainActivity.this, "No Network", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Log.e("Tapsell", "No Network Available");
                }

                @Override
                public void onExpiring(TapsellAd ad) {
                    App.isAdAvailable = false;
                    MainActivity.ad = null;
                    globalLoadAd(context, zoneId, catchType);
                }
            });
        }
    }

    public static void globalShowAd(Context context) {
        if (MainActivity.ad != null) {
            TapsellShowOptions showOptions = new TapsellShowOptions();
            showOptions.setBackDisabled(false);
            showOptions.setImmersiveMode(true);
            showOptions.setRotationMode(TapsellShowOptions.ROTATION_UNLOCKED);
            showOptions.setShowDialog(true);


            showOptions.setWarnBackPressedDialogMessage("ویدیو را ادامه میدهید؟ در صورت لغو تماشای ویدیو سکه ای دریافت نخواهید کرد");
            showOptions.setWarnBackPressedDialogMessageTextColor(Color.RED);
//                    showOptions.setWarnBackPressedDialogAssetTypefaceFileName("IranNastaliq.ttf");
            showOptions.setWarnBackPressedDialogPositiveButtonText("بله");
            showOptions.setWarnBackPressedDialogNegativeButtonText("خیر");
            showOptions.setWarnBackPressedDialogPositiveButtonBackgroundResId(R.drawable.rounded_circle_orange_border);
            showOptions.setWarnBackPressedDialogNegativeButtonBackgroundResId(R.drawable.rounded_circle_orange_border);
            showOptions.setWarnBackPressedDialogPositiveButtonTextColor(Color.BLACK);
            showOptions.setWarnBackPressedDialogNegativeButtonTextColor(Color.BLACK);
            showOptions.setWarnBackPressedDialogBackgroundResId(R.drawable.dialog_background);
            showOptions.setBackDisabledToastMessage("لطفا جهت بازگشت تا انتهای پخش ویدیو صبر کنید.");
//                    ad.show(MainActivity.this, showOptions);
            MainActivity.ad.show(context, showOptions, new TapsellAdShowListener() {
                @Override
                public void onOpened(TapsellAd ad) {
                    MainActivity.ad = null;
                    globalLoadAd(context, App.TapSelZoneId, TapsellAdRequestOptions.CACHE_TYPE_CACHED);

                    Log.e("MainActivity", "on ad opened");
                }

                @Override
                public void onClosed(TapsellAd ad) {
                    MainActivity.ad = null;
                    globalLoadAd(context, App.TapSelZoneId, TapsellAdRequestOptions.CACHE_TYPE_CACHED);
                    Log.e("MainActivity", "on ad closed");
                }
            });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setVariables();
        fm.beginTransaction().replace(R.id.fragmentHolder, homeFragment, "shopFragment").commit();
        currentItemId = R.id.action_home;
        progressDialog = new ProgressDialog(this);
        loadAd(App.TapSelZoneId, TapsellAdRequestOptions.CACHE_TYPE_CACHED);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() != currentItemId)
                switch (item.getItemId()) {
                    case R.id.action_advertise:
                        if (ad == null) {
                            loadAd(App.TapSelZoneId, TapsellAdRequestOptions.CACHE_TYPE_CACHED);

                        }
                        shwoAds();
                        break;
                    case R.id.action_navigation:
                        currentItemId = R.id.action_navigation;
                        fm.beginTransaction().replace(R.id.fragmentHolder, getCoinFragment, "shopFragment").commit();

                        break;
                    case R.id.action_home:
                        currentItemId = R.id.action_home;
                        fm.beginTransaction().replace(R.id.fragmentHolder, homeFragment, "shopFragment").commit();


                        break;
                    case R.id.action_purchase:
                        currentItemId = R.id.action_purchase;
                        fm.beginTransaction().replace(R.id.fragmentHolder, purchaseFragment, "shopFragment").commit();

                        break;
                    case R.id.action_shopping:
                        currentItemId = R.id.action_shopping;
                        fm.beginTransaction().replace(R.id.fragmentHolder, shopFragment, "shopFragment").commit();


                        break;
                }
            return true;
        });


        Tapsell.setRewardListener((tapsellAd, b) -> {
            if (b) {
                addCoin(0, 2);
            } else
            {}

        });
    }

    private void addCoin(int i, int o) {


        final String requestBody = JsonManager.addCoin(i, String.valueOf(o));

        StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "transaction/add_coin", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response1);
                    if (jsonRootObject.optBoolean("status")) {
                        App.followCoin = jsonRootObject.getInt("follow_coin");
                        App.likeCoin = jsonRootObject.getInt("like_coin");


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

    private void setVariables() {
        fm = getSupportFragmentManager();

    }

    private void loadAd(final String zoneId, final int catchType) {

        if (ad == null) {
            TapsellAdRequestOptions options = new TapsellAdRequestOptions(catchType);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("بارگزاری ...");
            progressDialog.show();
            Tapsell.requestAd(MainActivity.this, zoneId, options, new TapsellAdRequestListener() {
                @Override
                public void onError(String error) {
                    Toast.makeText(MainActivity.this, "ERROR:\n" + error, Toast.LENGTH_SHORT).show();
                    Log.e("Tapsell", "ERROR:" + error);
                    progressDialog.dismiss();
                }

                @Override
                public void onAdAvailable(TapsellAd ad) {

                    MainActivity.this.ad = ad;
                    App.isAdAvailable = true;
                    Log.e("Tapsell", "adId: " + (ad == null ? "NULL" : ad.getId()) + " available, zoneId: " + (ad == null ? "NULL" : ad.getZoneId()));
                    progressDialog.dismiss();
//                new AlertDialog.Builder(MainActivity.this).setTitle("Title").setMessage("Message").show();
                }

                @Override
                public void onNoAdAvailable() {
                    App.isAdAvailable = false;
                    Toast.makeText(MainActivity.this, "No Ad Available", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    Log.e("Tapsell", "No Ad Available");
                }

                @Override
                public void onNoNetwork() {
                    App.isAdAvailable = false;
                    Toast.makeText(MainActivity.this, "No Network", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Log.e("Tapsell", "No Network Available");
                }

                @Override
                public void onExpiring(TapsellAd ad) {
                    App.isAdAvailable = false;
                    MainActivity.this.ad = null;
                    loadAd(zoneId, catchType);
                }
            });
        }
    }

    private void shwoAds() {
        if (MainActivity.ad != null) {
            TapsellShowOptions showOptions = new TapsellShowOptions();
            showOptions.setBackDisabled(false);
            showOptions.setImmersiveMode(true);
            showOptions.setRotationMode(TapsellShowOptions.ROTATION_UNLOCKED);
            showOptions.setShowDialog(true);

            showOptions.setWarnBackPressedDialogMessage("ویدیو را ادامه میدهید؟ در صورت لغو تماشای ویدیو سکه ای دریافت نخواهید کرد");
            showOptions.setWarnBackPressedDialogMessageTextColor(Color.BLACK);
//                    showOptions.setWarnBackPressedDialogAssetTypefaceFileName("IranNastaliq.ttf");
            showOptions.setWarnBackPressedDialogPositiveButtonText("بله");
            showOptions.setWarnBackPressedDialogNegativeButtonText("خیر");
            showOptions.setWarnBackPressedDialogPositiveButtonTextColor(Color.BLACK);
            showOptions.setWarnBackPressedDialogNegativeButtonTextColor(Color.BLACK);
            showOptions.setWarnBackPressedDialogBackgroundResId(R.drawable.dialog_background);
            showOptions.setBackDisabledToastMessage("لطفا جهت بازگشت تا انتهای پخش ویدیو صبر کنید.");
//                    ad.show(MainActivity.this, showOptions);
            MainActivity.ad.show(this, showOptions, new TapsellAdShowListener() {
                @Override
                public void onOpened(TapsellAd ad) {
                    MainActivity.ad = null;
                    loadAd(App.TapSelZoneId, TapsellAdRequestOptions.CACHE_TYPE_CACHED);

                    Log.e("MainActivity", "on ad opened");
                }

                @Override
                public void onClosed(TapsellAd ad) {
                    MainActivity.ad = null;
                    loadAd(App.TapSelZoneId, TapsellAdRequestOptions.CACHE_TYPE_CACHED);
                    Log.e("MainActivity", "on ad closed");
                }
            });


        }
    }


}

