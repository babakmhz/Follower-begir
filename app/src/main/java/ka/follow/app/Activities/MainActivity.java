package ka.follow.app.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import ka.follow.util.IabHelper;
import ka.follow.util.IabResult;
import ka.follow.util.Purchase;
import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAd;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;

import static ka.follow.app.App.Base_URL;
import static ka.follow.app.App.requestQueue;

public class MainActivity extends AppCompatActivity implements PurchaseInterface,
        DirectPrchaseInterface,
        DirectPurchaseDialogInterface,
        ShopItemInterface, AddCoinMultipleAccount, SetPurchaseForOthersInterface {
    static final String TAG = "FollowerAPP";
    public static TapsellAd ad;
    public static ProgressDialog progressDialog;
    static String SKU_PREMIUM = "Item1";
    static int RC_REQUEST = 1372;
    private static ShopItem currentShop;
    boolean mIsPremium = false;
    public static IabHelper mHelper;
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener;
    int delay = 5000; //milliseconds
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private int currentItemId;
    private ActivityMainBinding binding;
    private int iapFollowCoin;
    private int iapLikeCoin;
    private DirectPurchaseDialogInterface callBackDirectPurchaseDialog;
    private String directOrderItemId = null, directOrderItemURL = null;
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
        mHelper = new IabHelper(this, BuildConfig.BAZAR_RSA);
        handlerCheckCoin = new Handler();

        handlerCheckCoin.postDelayed(runnableCheckCoin = new Runnable() {  /// TODO HAndler that checks coins
            public void run() {
                BroadcastManager.sendBroadcast(MainActivity.this);
                handlerCheckCoin.postDelayed(runnableCheckCoin, delay);
            }
        }, delay);


        try {
            mGotInventoryListener = (result, inventory) -> {
                Log.d(TAG, "Query inventory finished.");
                if (result.isFailure()) {
                    Log.d(TAG, "Failed to query inventory: " + result);
                    return;
                } else {
                    Log.d(TAG, "Query inventory was successful.");
                    // does the user have the premium upgrade?
                    mIsPremium = inventory.hasPurchase(SKU_PREMIUM);
                    if (mIsPremium) {
                        MasrafSeke(inventory.getPurchase(SKU_PREMIUM));
                    }
                    // update UI accordingly

                    Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
                }

                Log.d(TAG, "Initial inventory query finished; enabling main UI.");
            };
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mPurchaseFinishedListener = (result, purchase) -> {
                if (result.isFailure()) {
                    Log.d(TAG, "Error purchasing: " + result);
                    return;
                } else if (purchase.getSku().equals(App.SkuSpecialWheel)) {
                    new SharedPreferences(MainActivity.this).setSpeccialWhhel(true);
                    Toast.makeText(MainActivity.this, "خرید موفق", Toast.LENGTH_SHORT).show();

                    MasrafSeke(purchase);

                } else if (purchase.getSku().equals(Config.SKUSpecialBanner)) {
                    Toast.makeText(MainActivity.this, "با تشکر از خرید شما", Toast.LENGTH_SHORT).show();
                    addCoin(0, iapLikeCoin);
                    addCoin(1, iapFollowCoin);
                    MasrafSeke(purchase);
                    Intent intent = new Intent("com.journaldev.broadcastreceiver.Update");
                    sendBroadcast(intent);
                } else if (purchase.getSku().equals(Config.skuFirstLike)) {
                    increaseBeforeOrder(0, 100);
                    MasrafSeke(purchase);
                } else if (purchase.getSku().equals(Config.skuSecondLike)) {
                    increaseBeforeOrder(0, 500);
                    MasrafSeke(purchase);
                } else if (purchase.getSku().equals(Config.skuThirdLike)) {
                    increaseBeforeOrder(0, 1000);
                    MasrafSeke(purchase);
                } else if (purchase.getSku().equals(Config.skuFourthLike)) {
                    increaseBeforeOrder(0, 2000);
                    MasrafSeke(purchase);
                } else if (purchase.getSku().equals(Config.skuFifthLike)) {
                    increaseBeforeOrder(0, 3000);
                    MasrafSeke(purchase);
                } else if (purchase.getSku().equals(Config.skuFirstComment)) {
                    increaseBeforeOrder(2, 50);
                    MasrafSeke(purchase);
                } else if (purchase.getSku().equals(Config.skuSecondComment)) {
                    increaseBeforeOrder(2, 100);
                    MasrafSeke(purchase);
                } else if (purchase.getSku().equals(Config.skuThirdComment)) {
                    increaseBeforeOrder(2, 200);
                    MasrafSeke(purchase);
                } else if (purchase.getSku().equals(Config.skuFirstFollow)) {
                    increaseBeforeOrder(1, 180);
                    MasrafSeke(purchase);
                } else if (purchase.getSku().equals(Config.skuSecondFollow)) {
                    increaseBeforeOrder(1, 500);
                    MasrafSeke(purchase);
                } else if (purchase.getSku().equals(Config.skuThirdFollow)) {
                    increaseBeforeOrder(1, 1200);
                    MasrafSeke(purchase);
                } else if (purchase.getSku().equals(Config.skuFourthFollow)) {
                    increaseBeforeOrder(1, 2600);
                    MasrafSeke(purchase);
                } else if (purchase.getSku().equals(Config.skuFifthFollow)) {
                    increaseBeforeOrder(1, 3800);
                    MasrafSeke(purchase);
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            mHelper.startSetup(result -> {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
                mHelper.queryInventoryAsync(mGotInventoryListener);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Starting setup.");

        Tapsell.setRewardListener((tapsellAd, b) -> {
            if (b) {
                Toast.makeText(this, "2 سکه لایک به شما افزوده شد", Toast.LENGTH_SHORT).show();
                addCoin(0, 2);

            } else {
            }

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
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
       /* if (requestCode == Config.requestShopItems) {
            if (shopItemAmount != 0)
                addCoin(shopItemType, shopItemAmount);
        }*/

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
            if (requestCode == Config.requestShopItems && isSuccessShop) {
                if (shopItemAmount != 0) {
                    addCoin(shopItemType, shopItemAmount);

                }
            }
        }
    }


    @Override
    public void onDestroy() {
        //از سرویس در زمان اتمام عمر activity قطع شوید
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }


    private void MasrafSeke(Purchase kala) {
        // برای اینکه کاربر فقط یکبار بتواند از کالای فروشی استفاده کند
        // باید بعد از خرید آن کالا را مصرف کنیم
        // در غیر اینصورت کاربر با یکبار خرید محصول می تواند چندبار از آن استفاده کند
        mHelper.consumeAsync(kala, new IabHelper.OnConsumeFinishedListener() {
            @Override
            public void onConsumeFinished(Purchase purchase, IabResult result) {
                if (result.isSuccess())
                    //Toast.makeText(MainActivity.this, "مصرف شد", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "NATIJE masraf: " + result.getMessage() + result.getResponse());

            }
        });
    }

    @Override
    public void buyItem(String sdk, int requestCode) {
        if (!App.isBazarInstalled) {
            Toast.makeText(this, "برای پرداخت درون برنامه ای کافه بازار را نصب کنید", Toast.LENGTH_LONG).show();
            return;

        }
        SKU_PREMIUM = sdk;
        RC_REQUEST = requestCode;
        mHelper.launchPurchaseFlow(this, sdk, RC_REQUEST, mPurchaseFinishedListener, "extra info");

    }

    @Override
    public void IABPurchase(ShopItem shopItem) {
        if (!App.isBazarInstalled) {
            Toast.makeText(this, "برای پرداخت درون برنامه ای کافه بازار را نصب کنید", Toast.LENGTH_LONG).show();
            return;

        }
        mHelper.launchPurchaseFlow(this, shopItem.getSku(), shopItem.getReturnValue(), mPurchaseFinishedListener, "extra info");

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
        mHelper.launchPurchaseFlow(this, sku, RC_REQUEST, mPurchaseFinishedListener, "extra info");


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
        mHelper.launchPurchaseFlow(this, sku, requestCode, mPurchaseFinishedListener, "extra info");

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
        mHelper.launchPurchaseFlow(this, sku, RequestCode, mPurchaseFinishedListener, "extra info");
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
}

