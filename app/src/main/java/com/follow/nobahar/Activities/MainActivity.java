package com.follow.nobahar.Activities;

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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

import java.io.UnsupportedEncodingException;

import io.nivad.iab.BillingProcessor;
import io.nivad.iab.MarketName;
import io.nivad.iab.TransactionDetails;
import com.follow.nobahar.App;
import com.follow.nobahar.BuildConfig;
import com.follow.nobahar.CheckUnFollowService;
import com.follow.nobahar.Dialog.PurchasePackages.DirectPrchaseInterface;
import com.follow.nobahar.Fragments.GetCoin.GetCoinFragment;
import com.follow.nobahar.Fragments.HomeFragment;
import com.follow.nobahar.Fragments.Offers.ShopFragment;
import com.follow.nobahar.Fragments.Purchase.PurchaseFragment;
import com.follow.nobahar.Interface.AddCoinMultipleAccount;
import com.follow.nobahar.Interface.DirectPurchaseDialogInterface;
import com.follow.nobahar.Interface.PurchaseInterface;
import com.follow.nobahar.Interface.SetPurchaseForOthersInterface;
import com.follow.nobahar.Interface.ShopItemInterface;
import com.follow.nobahar.Manager.BroadcastManager;
import com.follow.nobahar.Manager.Config;
import com.follow.nobahar.Manager.DataBaseHelper;
import com.follow.nobahar.Manager.SharedPreferences;
import com.follow.nobahar.Models.ShopItem;
import com.follow.nobahar.R;
import com.follow.nobahar.Retrofit.ApiClient;
import com.follow.nobahar.Retrofit.ApiInterface;
import com.follow.nobahar.Retrofit.UserCoin;
import com.follow.nobahar.databinding.ActivityMainBinding;
import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAd;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private ApiInterface apiInterface;

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
        ApiClient.getClient();
        apiInterface = ApiClient.retrofit.create(ApiInterface.class);

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
        FirebaseApp.initializeApp(this);


        /// TODO HAndler that checks coins
        handlerCheckCoin.postDelayed(runnableCheckCoin = () -> {
            BroadcastManager.sendBroadcast(MainActivity.this);
            handlerCheckCoin.postDelayed(runnableCheckCoin, delay);
        }, delay);


        binding.llHome.setOnClickListener(v -> setActive(0));
        binding.llPurchase.setOnClickListener(v -> setActive(1));
        binding.llGetCoin.setOnClickListener(v -> setActive(2));
        binding.llShop.setOnClickListener(v -> setActive(3));
        binding.llAds.setOnClickListener(v -> setActive(4));


        Log.d(TAG, "Starting setup.");

        Tapsell.setRewardListener((tapsellAd, b) -> {
            if (b) {
                Toast.makeText(this, "2 سکه لایک به شما افزوده شد", Toast.LENGTH_SHORT).show();
                addCoin(0, 2,apiInterface);

            } else {
            }

        });

//        mNivadBilling = new BillingProcessor(this, BuildConfig.BAZAR_RSA,
//                "dae76086-f588-4a91-b530-e2738bd21064",
//                "qJdIoxZPuNudYlLYxvqm5xdIEohzjCBOyMv3RmUCvRzjrvFS2rD1c7UtK4Al0RE0", MarketName.CAFE_BAZAAR, this); // مقدار دهی در انتهای onCreate
                mNivadBilling = new BillingProcessor(this, BuildConfig.BAZAR_RSA,
                null,
                null, MarketName.CAFE_BAZAAR, this); // مقدار دهی در انتهای onCreate
        Log.i(TAG, "onCreate: ");
        if (new DataBaseHelper(this).getAllFollowings().size() > 0) {
            Intent intent = new Intent(this, CheckUnFollowService.class);
            startService(intent);
        }
    }

    private void setActive(int position) {
        switch (position) {
            case 0:
                reset();
                binding.llHome.setBackground(getResources().getDrawable(R.drawable.rounded_white_border));
                binding.tvHome.setTextColor(getResources().getColor(R.color.white));
                binding.bottomNavigation.setSelectedItemId(R.id.action_home);
                break;
            case 1:
                reset();
                binding.llPurchase.setBackground(getResources().getDrawable(R.drawable.rounded_white_border));
                binding.tvPurchase.setTextColor(getResources().getColor(R.color.white));
                binding.bottomNavigation.setSelectedItemId(R.id.action_purchase);
                break;
            case 2:
                reset();
                binding.llGetCoin.setBackground(getResources().getDrawable(R.drawable.rounded_white_border));
                binding.tvGetCoin.setTextColor(getResources().getColor(R.color.white));
                binding.bottomNavigation.setSelectedItemId(R.id.action_navigation);

                break;
            case 3:
                reset();
                binding.llShop.setBackground(getResources().getDrawable(R.drawable.rounded_white_border));
                binding.tvShop.setTextColor(getResources().getColor(R.color.white));
                binding.bottomNavigation.setSelectedItemId(R.id.action_shopping);

                break;
            case 4:
                reset();
                binding.llAds.setBackground(getResources().getDrawable(R.drawable.rounded_white_border));
                binding.tvAds.setTextColor(getResources().getColor(R.color.white));
                binding.bottomNavigation.setSelectedItemId(R.id.action_advertise);

                break;
        }
    }

    private void reset() {
        binding.llHome.setBackground(getResources().getDrawable(R.drawable.rounded_main_un_selected));
        binding.llAds.setBackground(getResources().getDrawable(R.drawable.rounded_main_un_selected));
        binding.llGetCoin.setBackground(getResources().getDrawable(R.drawable.rounded_main_un_selected));
        binding.llPurchase.setBackground(getResources().getDrawable(R.drawable.rounded_main_un_selected));
        binding.llShop.setBackground(getResources().getDrawable(R.drawable.rounded_main_un_selected));
        binding.tvAds.setTextColor(getResources().getColor(R.color.black));
        binding.tvHome.setTextColor(getResources().getColor(R.color.black));
        binding.tvGetCoin.setTextColor(getResources().getColor(R.color.black));
        binding.tvPurchase.setTextColor(getResources().getColor(R.color.black));
        binding.tvShop.setTextColor(getResources().getColor(R.color.black));
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
                    addCoin(0, Config.bannerLikeCoinCount, apiInterface);
                    addCoin(1, Config.bannerFollowCoin, apiInterface);
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
                    addCoin(Config.shopType, Config.shopCounts, apiInterface);

                }
            }
        }

    }


    private void addCoin(int i, int o, ApiInterface apiInterface) {


        byte[] data = new byte[0];
        try {
            data = String.valueOf(o).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
        apiInterface.AddCoin(App.UUID, App.Api_Token, String.valueOf(i), base64).enqueue(new Callback<UserCoin>() {
            @Override
            public void onResponse(Call<UserCoin> call, Response<UserCoin> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getStatus()) {
                            App.followCoin = response.body().getFollowCoin();
                            App.likeCoin = response.body().getLikeCoin();
                            Intent intent = new Intent("com.journaldev.broadcastreceiver.Update");
                            Toast.makeText(MainActivity.this, "سکه های شما افزایش یافت..", Toast.LENGTH_SHORT).show();
                            sendBroadcast(intent);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserCoin> call, Throwable t) {

            }
        });


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


        byte[] data = new byte[0];
        try {
            data = String.valueOf(count).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
        apiInterface.AddCoin(App.UUID, App.Api_Token, String.valueOf(type), base64).enqueue(new Callback<UserCoin>() {
            @Override
            public void onResponse(Call<UserCoin> call, Response<UserCoin> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getStatus()) {
                            purchaseAfterIncrease(type, count,apiInterface);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserCoin> call, Throwable t) {

            }
        });


    }

    private void purchaseAfterIncrease(int type, int count, ApiInterface apiInterface) {

        apiInterface.SetOrder(App.UUID,App.Api_Token,type,directOrderItemId,count,count,directOrderItemURL).enqueue(new Callback<UserCoin>() {
            @Override
            public void onResponse(Call<UserCoin> call, Response<UserCoin> response) {
                if (response.isSuccessful())
                {
                    if (response.body()!=null)
                    {
                        if (response.body().getStatus())
                        {
                            Toast.makeText(MainActivity.this, "سفارش شما با موفقیت ثبت شد", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserCoin> call, Throwable t) {

            }
        });



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
        addCoin(0, 1,apiInterface);

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

