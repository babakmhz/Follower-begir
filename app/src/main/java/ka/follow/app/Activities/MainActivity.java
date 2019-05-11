package ka.follow.app.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.nivad.iab.BillingProcessor;
import io.nivad.iab.MarketName;
import io.nivad.iab.TransactionDetails;
import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAd;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;
import ka.follow.app.App;
import ka.follow.app.BuildConfig;
import ka.follow.app.Dialog.PurchasePackages.DirectPrchaseInterface;
import ka.follow.app.Fragments.GetCoin.GetCoinFragment;
import ka.follow.app.Fragments.HomeFragment;
import ka.follow.app.Fragments.Offers.ShopFragment;
import ka.follow.app.Fragments.Purchase.PurchaseFragment;
import ka.follow.app.Interface.AddCoinMultipleAccount;
import ka.follow.app.Interface.DirectPurchaseDialogInterface;
import ka.follow.app.Interface.PurchaseInterface;
import ka.follow.app.Interface.SetPurchaseForOthersInterface;
import ka.follow.app.Interface.ShopItemInterface;
import ka.follow.app.Manager.BroadcastManager;
import ka.follow.app.Manager.Config;
import ka.follow.app.Manager.JsonManager;
import ka.follow.app.Manager.SharedPreferences;
import ka.follow.app.Models.ShopItem;
import ka.follow.app.R;
import ka.follow.app.databinding.ActivityMainBinding;

import static ka.follow.app.App.Base_URL;
import static ka.follow.app.App.requestQueue;

public class MainActivity extends AppCompatActivity implements PurchaseInterface,
        DirectPrchaseInterface,
        DirectPurchaseDialogInterface,
        ShopItemInterface, AddCoinMultipleAccount, SetPurchaseForOthersInterface, BillingProcessor.IBillingHandler {
    static final String TAG = "FollowerAPP";
    public static TapsellAd ad;
    public static ProgressDialog progressDialog;
    static String SKU_PREMIUM = "Item1";
    static int RC_REQUEST = 1372;
    private static ShopItem currentShop;
    boolean mIsPremium = false;
    int delay = 5000; //milliseconds
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private int currentItemId;
    private ActivityMainBinding binding;
    private int iapFollowCoin;
    private int iapLikeCoin;
    private DirectPurchaseDialogInterface callBackDirectPurchaseDialog;
    public static String directOrderItemId = null, directOrderItemURL = null;
    private int directOrderCount = 0;
    private ShopItemInterface callBackShopItem;
    private AddCoinMultipleAccount addCoinMultipleAccount;
    private PurchaseInterface callBack;
    private int shopItemType = 0;
    private int shopItemAmount = 0;
    private Handler handlerCheckCoin;
    private Runnable runnableCheckCoin;
    private boolean doubleBackToExitPressedOnce = false;
    private String currentSKU;
    private boolean isSuccessShop = false;
    public static BillingProcessor mNivadBilling;

    public static void globalLoadAd(Context context, final String zoneId, final int catchType) {
        if (MainActivity.ad == null) {
            TapsellAdRequestOptions options = new TapsellAdRequestOptions(catchType);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("بارگزاری ...");

            //progressDialog.show(); TODO

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
            showOptions.setWarnBackPressedDialogMessageTextColor(Color.WHITE);
            showOptions.setWarnBackPressedDialogAssetTypefaceFileName(String.valueOf(R.font.iran_yekan_light));
            showOptions.setWarnBackPressedDialogPositiveButtonText("بله");
            showOptions.setWarnBackPressedDialogNegativeButtonText("خیر");
            showOptions.setWarnBackPressedDialogNegativeButtonTextColor(R.color.white);
            showOptions.setWarnBackPressedDialogNegativeButtonTextColor(R.color.white);

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
        callBack = this;
        callBackDirectPurchaseDialog = this;
        callBackShopItem = this;
        addCoinMultipleAccount = this;
        fm.beginTransaction().replace(R.id.fragmentHolder, new HomeFragment(callBack), "shopFragment").commit();
        currentItemId = R.id.action_home;
        progressDialog = new ProgressDialog(this);
        App.context = this;
        App.currentActivity=this;
        loadAd(App.TapSelZoneId, TapsellAdRequestOptions.CACHE_TYPE_CACHED);
        //Bottom navigation
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
                        fm.beginTransaction().replace(R.id.fragmentHolder, new GetCoinFragment(addCoinMultipleAccount), "shopFragment").commit();

                        break;
                    case R.id.action_home:
                        currentItemId = R.id.action_home;
                        fm.beginTransaction().replace(R.id.fragmentHolder, new HomeFragment(callBack), "shopFragment").commit();


                        break;
                    case R.id.action_purchase:
                        currentItemId = R.id.action_purchase;
                        fm.beginTransaction().replace(R.id.fragmentHolder, new PurchaseFragment(callBackDirectPurchaseDialog), "shopFragment").commit();

                        break;
                    case R.id.action_shopping:
                        currentItemId = R.id.action_shopping;
                        fm.beginTransaction().replace(R.id.fragmentHolder, new ShopFragment(callBack, callBackShopItem), "shopFragment").commit();


                        break;
                }
            return true;
        });
        handlerCheckCoin = new Handler();

        handlerCheckCoin.postDelayed(runnableCheckCoin = new Runnable() {  /// TODO HAndler that checks coins
            public void run() {
                BroadcastManager.sendBroadcast(MainActivity.this);
                handlerCheckCoin.postDelayed(runnableCheckCoin, delay);
            }
        }, delay);




        Log.d(TAG, "Starting setup.");

        Tapsell.setRewardListener((tapsellAd, b) -> {
            if (b) {
                Toast.makeText(this, "2 سکه لایک به شما افزوده شد", Toast.LENGTH_SHORT).show();
                addCoin(0, 2);

            } else {
            }

        });

        mNivadBilling = new BillingProcessor(this, "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDB1YN8n11QuwWpL+XHqqqdGKgYG9d4FLglhJqfLEeV7FFXShc06oA1qbu6cFBLSqd31aK1k57ctyuitaZVOm3gPXlB2kYhfGbVz6R33G0h0o5HqRXgyavf3JvP5YMBvTJa4anxPa1CcOjxjPwazLvsc0w/aUf/Xg++q1OUVAc/Ib46kChsZmJ/D1o8WHsxi37hW+aKud18WDfBoeNdvOnMpAu7t6qX6pNW2uMgQlUCAwEAAQ==",
                "99cf884d-3517-48d4-8d3d-0e32062c4e66",
                "5ticxVqTG05PfGyQ4pacn4nCnCYnBOqicuOywhcwRUoBHbMwtO2Gae1BwqX6T1SW", MarketName.CAFE_BAZAAR, this); // مقدار دهی در انتهای onCreate
        Log.i(TAG, "onCreate: ");
    }

    private void checkNivadOrder(String sku, TransactionDetails transactionDetails) {


        if (mNivadBilling.isPurchased(App.SkuSpecialWheel)) {
            boolean success = mNivadBilling.consumePurchase(App.SkuSpecialWheel);
            {
                if (success) {
                    new SharedPreferences(MainActivity.this).setSpeccialWhhel(true);
                    Toast.makeText(MainActivity.this, "گردونه ویژه با موفقیت خریداری شد", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (mNivadBilling.isPurchased(Config.SKUSpecialBanner)) {
            boolean success = mNivadBilling.consumePurchase(Config.SKUSpecialBanner);
            {
                if (success) {
                    addCoin(0, Config.bannerLikeCoinCount);
                    addCoin(1, Config.bannerFollowCoin);
                    Intent intent = new Intent("com.journaldev.broadcastreceiver.Update");
                    sendBroadcast(intent);
                    Toast.makeText(MainActivity.this, "با تشکر از خرید شما", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (mNivadBilling.isPurchased(Config.skuFirstLike)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuFirstLike);
            {
                if (success) {
                    increaseBeforeOrder(0, 100);
                }
            }
        } else if (mNivadBilling.isPurchased(Config.skuSecondLike)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuSecondLike);
            {
                if (success) {
                    increaseBeforeOrder(0, 500);

                }
            }
        } else if (mNivadBilling.isPurchased(Config.skuThirdLike)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuThirdLike);
            {
                if (success) {
                    increaseBeforeOrder(0, 1000);

                }
            }
        } else if (mNivadBilling.isPurchased(Config.skuFourthLike)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuFourthLike);
            {
                if (success) {
                    increaseBeforeOrder(0, 2000);

                }
            }
        } else if (mNivadBilling.isPurchased(Config.skuFifthLike)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuFifthLike);
            {
                if (success) {
                    increaseBeforeOrder(0, 3000);

                }
            }
        }
//Comments
        else if (mNivadBilling.isPurchased(Config.skuFirstComment)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuFirstComment);
            {
                if (success) {
                    increaseBeforeOrder(2, 50);

                }
            }
        } else if (mNivadBilling.isPurchased(Config.skuSecondComment)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuSecondComment);
            {
                if (success) {
                    increaseBeforeOrder(2, 100);

                }
            }
        } else if (mNivadBilling.isPurchased(Config.skuThirdComment)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuThirdComment);
            {
                if (success) {
                    increaseBeforeOrder(2, 200);

                }
            }
        } else if (mNivadBilling.isPurchased(Config.skuThirdComment)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuThirdComment);
            {
                if (success) {
                    increaseBeforeOrder(2, 200);

                }
            }
        }
        //Follow

        else if (mNivadBilling.isPurchased(Config.skuFirstFollow)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuFirstFollow);
            {
                if (success) {
                    increaseBeforeOrder(1, 180);

                }
            }
        } else if (mNivadBilling.isPurchased(Config.skuSecondFollow)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuSecondFollow);
            {
                if (success) {
                    increaseBeforeOrder(1, 500);

                }
            }
        } else if (mNivadBilling.isPurchased(Config.skuThirdFollow)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuThirdFollow);
            {
                if (success) {
                    increaseBeforeOrder(1, 1200);

                }
            }
        } else if (mNivadBilling.isPurchased(Config.skuFourthFollow)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuFourthFollow);
            {
                if (success) {
                    increaseBeforeOrder(1, 2600);

                }
            }
        } else if (mNivadBilling.isPurchased(Config.skuFifthFollow)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuFifthFollow);
            {
                if (success) {
                    increaseBeforeOrder(1, 3800);

                }
            }
        }
//View

        else if (mNivadBilling.isPurchased(Config.skuFirstVIew)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuFirstVIew);
            {
                if (success) {
                    increaseBeforeOrder(3, 100);

                }
            }
        } else if (mNivadBilling.isPurchased(Config.skuSecondView)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuSecondView);
            {
                if (success) {
                    increaseBeforeOrder(3, 200);

                }
            }
        } else if (mNivadBilling.isPurchased(Config.skuThirdComment)) {
            boolean success = mNivadBilling.consumePurchase(Config.skuThirdComment);
            {
                if (success) {
                    increaseBeforeOrder(3, 300);

                }
            }
        } else if (mNivadBilling.isPurchased(Config.shopSku)) {
            boolean success = mNivadBilling.consumePurchase(Config.shopSku);
            {
                if (success) {
                    addCoin(Config.shopType, Config.shopCounts);

                }
            }
        }

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
                        Intent intent = new Intent("com.journaldev.broadcastreceiver.Update");
                        Toast.makeText(this, "سکه های شما افزایش یافت..", Toast.LENGTH_SHORT).show();
                        sendBroadcast(intent);
                        shopItemAmount = 0;
                        shopItemType = 0;


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
            //progressDialog.show(); TODO
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
                    Toast.makeText(MainActivity.this, "رسانه فعالی موجود نیست", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    Log.e("Tapsell", "No Ad Available");
                }

                @Override
                public void onNoNetwork() {
                    App.isAdAvailable = false;
                    Toast.makeText(MainActivity.this, "خطا در اتصال به شبکه", Toast.LENGTH_SHORT).show();
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
            showOptions.setWarnBackPressedDialogMessageTextColor(Color.WHITE);
            showOptions.setWarnBackPressedDialogAssetTypefaceFileName(String.valueOf(R.font.iran_yekan_light));
            showOptions.setWarnBackPressedDialogPositiveButtonText("بله");
            showOptions.setWarnBackPressedDialogNegativeButtonText("خیر");
            showOptions.setWarnBackPressedDialogNegativeButtonTextColor(R.color.white);
            showOptions.setWarnBackPressedDialogNegativeButtonTextColor(R.color.white);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mNivadBilling.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroy() {
        if (mNivadBilling != null)
            mNivadBilling.release();
        super.onDestroy();
    }



    @Override
    public void buyItem(String sdk, int requestCode) {
        if (!App.isBazarInstalled) {
            Toast.makeText(this, "برای پرداخت درون برنامه ای کافه بازار را نصب کنید", Toast.LENGTH_LONG).show();
            return;

        }
        SKU_PREMIUM = sdk;
        RC_REQUEST = requestCode;
    }

    @Override
    public void IABPurchase(ShopItem shopItem) {

    }

    private void increaseBeforeOrder(int type, int count) {


        final String requestBody = JsonManager.addCoin(type, String.valueOf(count * 2));

        StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "transaction/add_coin", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response1);
                    if (jsonRootObject.optBoolean("status")) {
                        purchaseAfterIncrease(type, count);


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

    private void purchaseAfterIncrease(int type, int count) {
        final String requestBody = JsonManager.submitOrder(type, directOrderItemId, directOrderItemURL, count);

        StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "transaction/set", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response1);
                    if (jsonRootObject.optBoolean("status")) {
                        Toast.makeText(this, "سفارش شما با موفقیت ثبت شد", Toast.LENGTH_SHORT).show();

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




    @Override
    public void specialBanner(String sku, int requestCode, int followCoin, int LikeCoin) {
        if (!App.isBazarInstalled) {
            Toast.makeText(this, "برای پرداخت درون برنامه ای کافه بازار را نصب کنید", Toast.LENGTH_LONG).show();
            return;

        }
        SKU_PREMIUM = sku;
        RC_REQUEST = requestCode;
        iapFollowCoin = followCoin;
        iapLikeCoin = LikeCoin;


    }

    @Override
    public void directPurchase(String sku, int requestCode, String imageUrl, String postId, int count) {
        if (!App.isBazarInstalled) {
            Toast.makeText(this, "برای پرداخت درون برنامه ای کافه بازار را نصب کنید", Toast.LENGTH_LONG).show();
            return;

        }
        directOrderItemId = postId;
        directOrderItemURL = imageUrl;
        directOrderCount = count;

    }

    @Override
    public void shopItemBuy(String sku, int type, int amount, int RequestCode) {
        if (!App.isBazarInstalled) {
            Toast.makeText(this, "برای پرداخت درون برنامه ای کافه بازار را نصب کنید", Toast.LENGTH_LONG).show();
            return;

        }
        this.isSuccessShop = true;
        this.shopItemType = type;
        this.shopItemAmount = amount;
        this.currentSKU = sku;
    }

    @Override
    public void addCoinMultipleAccount(int type) {
        addCoin(0, 1);

    }

    @Override
    public void showOtherProfileDialog(String userId) {
        ////TODO
    }

    @Override
    protected void onStop() {
        handlerCheckCoin.removeCallbacks(runnableCheckCoin);
        //stop handler
        super.onStop();

    }

    @Override
    protected void onPause() {
        handlerCheckCoin.removeCallbacks(runnableCheckCoin); //stop handler
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handlerCheckCoin.postDelayed(runnableCheckCoin = () -> {
            BroadcastManager.sendBroadcast(MainActivity.this);
            handlerCheckCoin.postDelayed(runnableCheckCoin, delay);
        }, delay);
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "برای خروج مجدد کلید برگشت را فشار دهید", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 4000);
    }

    @Override
    public void onProductPurchased(String s, TransactionDetails transactionDetails) {
        checkNivadOrder(s, transactionDetails);

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int i, Throwable throwable) {
        switch (i) {
            case 101:
                App.Toast(MainActivity.this, "مشکلی پیش آمده ، از نصب بودن کافه بازار بر روی دستگاهتان مطمئن شده و مجددا تلاش نمایید !");
                break;
            case 102:
                App.Toast(MainActivity.this, "مشکلی پیش آمده ، مجددا تلاش نمایید !");
                break;
            case 103:
                App.Toast(MainActivity.this, "مشکلی پیش آمده ، مجددا تلاش نمایید !");
                break;
            case 203:
                App.Toast(MainActivity.this, "مشکلی پیش آمده ، از اتصال اینترنت مطمئن شده و مجددا تلاش نمایید !");
                break;
            case 204:
                App.Toast(MainActivity.this, "مشکلی پیش آمده ، از اتصال اینترنت مطمئن شده و مجددا تلاش نمایید !");
                break;
            case 205:
                App.Toast(MainActivity.this, "این برنامه غیر قابل هک می باشد ، لطفا از راه صحیح اقدام به خرید نمایید !اخطار آخر ...");
                break;
            case 110:
                App.Toast(MainActivity.this, "مشکلی پیش آمده ، مجددا تلاش نمایید !");
                break;
        }


    }

    @Override
    public void onBillingInitialized() {
        App.IsBazarExists = true;
    }
}

