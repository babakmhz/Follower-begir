package ka.follow.v4.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ka.follow.v4.Activities.MainActivity;
import ka.follow.v4.Adapters.HorizontalAccountsListAdapter;
import ka.follow.v4.App;
import ka.follow.v4.Dialog.AboutUsDialog;
import ka.follow.v4.Dialog.AccountStatisticsDialog;
import ka.follow.v4.Dialog.AuthenticationDialog;
import ka.follow.v4.Dialog.FirstPageUpdateDialog;
import ka.follow.v4.Dialog.LuckyWheelPickerDialog;
import ka.follow.v4.Dialog.ManageAccountsDialog;
import ka.follow.v4.Dialog.NetworkErrorDialog;
import ka.follow.v4.Dialog.ReviewOrdersDialog;
import ka.follow.v4.Dialog.SearchDialog;
import ka.follow.v4.Dialog.SpecialLuckyWheelPickerDialog;
import ka.follow.v4.Dialog.TicketDialog;
import ka.follow.v4.Dialog.TopUsersDialog;
import ka.follow.v4.Dialog.TransferCoinDialog;
import ka.follow.v4.Interface.AccountChangerInterface;
import ka.follow.v4.Interface.AccountOptionChooserInterface;
import ka.follow.v4.Interface.PurchaseInterface;
import ka.follow.v4.Interface.ValueUpdaterBroadCast;
import ka.follow.v4.Manager.Config;
import ka.follow.v4.Manager.DataBaseHelper;
import ka.follow.v4.Manager.JsonManager;
import ka.follow.v4.Manager.SharedPreferences;
import ka.follow.v4.Models.User;
import ka.follow.v4.R;
import ka.follow.v4.Retrofit.ApiClient;
import ka.follow.v4.Retrofit.ApiInterface;
import ka.follow.v4.Retrofit.Button;
import ka.follow.v4.Retrofit.FirstPage;
import ka.follow.v4.Retrofit.ForceFollow;
import ka.follow.v4.Retrofit.Login;
import ka.follow.v4.Retrofit.SpecialBanner;
import ka.follow.v4.data.InstagramUser;
import ka.follow.v4.data.UserData;
import ka.follow.v4.databinding.FragmentHomeBinding;
import ka.follow.v4.instaAPI.InstaApiException;
import ka.follow.v4.instaAPI.InstagramApi;
import ka.follow.v4.parser.UserParser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static ka.follow.v4.App.TAG;
import static ka.follow.v4.App.isNetworkAvailable;
import static ka.follow.v4.App.requestQueue;
import static ka.follow.v4.Retrofit.ApiClient.retrofit;

@SuppressLint("ValidFragment")
public class HomeFragment extends Fragment implements AccountChangerInterface, AccountOptionChooserInterface {

    private  PurchaseInterface callBackPurchase;
    private View view;
    private FragmentHomeBinding binding;
    private InstagramApi api = InstagramApi.getInstance();
    private UserData userData = UserData.getInstance();
    private ProgressDialog progressDoalog;
    private DataBaseHelper dbHeplper;
    private SQLiteDatabase db;
    private android.content.SharedPreferences shared;
    private android.content.SharedPreferences.Editor editor;
    private String next_max_id;
    private String profilePicURL;
    private AccountChangerInterface callBack;
    private String specialBannerItemId;
    private AccountOptionChooserInterface accountOptionCallBack;

    public HomeFragment() {
    }

    public HomeFragment(PurchaseInterface callBack) {
        this.callBackPurchase = callBack;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_home, container, false);
        view = binding.getRoot();
        dbHeplper = new DataBaseHelper(App.context);
        shared = App.currentActivity.getSharedPreferences("UserPrefs", MODE_PRIVATE);
        editor = shared.edit();
        callBack = this;
        accountOptionCallBack = this;
        ApiClient.getClient();
        //ToDo Remove
        //new SharedPreferences(App.currentActivity).setSpeccialWhhel(true);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        try {
            dbHeplper.createDatabase();
            dbHeplper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        db = dbHeplper.getWritableDatabase();
        ValueUpdaterBroadCast.bindListener(() -> {
            binding.tvLikeCoinCount.setText(App.likeCoin + "");
            binding.tvFollowerCoinCount.setText(App.followCoin + "");

        });
        getUserInfo(apiInterface);
        doForceFollow(apiInterface);



        binding.imageView3.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);
                dialog.show(getChildFragmentManager(), "");
                return;
            }
            LuckyWheelPickerDialog dialog = new LuckyWheelPickerDialog();
            dialog.show(getChildFragmentManager(), "");
        });
        binding.tvTransferCoin.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);
                dialog.show(getChildFragmentManager(), "");
                return;
            }
            TransferCoinDialog transferCoinDialog = new TransferCoinDialog();
            transferCoinDialog.show(getChildFragmentManager(), "");
        });

        binding.tvOrders.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }
            ReviewOrdersDialog transferCoinDialog = new ReviewOrdersDialog();
            transferCoinDialog.show(getChildFragmentManager(), "");
        });

        binding.tvAccountInfo.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }

            AccountStatisticsDialog accountStatisticsDialog = new AccountStatisticsDialog(profilePicURL);
            accountStatisticsDialog.show(getChildFragmentManager(), "");


        });

        binding.tvManageAccounts.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }
            ManageAccountsDialog dialog = new ManageAccountsDialog(callBack);
            dialog.show(getChildFragmentManager(), "");
        });

        binding.tvTopUsers.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }
            TopUsersDialog topUsersDialog = new TopUsersDialog();
            topUsersDialog.show(getChildFragmentManager(), "");
        });

        binding.imvLogOut.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }
            signOut();
        });
        binding.tvLogOut.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }
            signOut();

        });

        binding.tvSupport.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }
            TicketDialog dialog = new TicketDialog();
            dialog.show(getChildFragmentManager(), "");
        });
        binding.fourthContainer.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }
            MainActivity.globalShowAd(getActivity());

        });

        binding.imageView.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }

            if (new SharedPreferences(App.currentActivity).getSpecialWheel()) {
                SpecialLuckyWheelPickerDialog dialog = new SpecialLuckyWheelPickerDialog();
                dialog.show(getChildFragmentManager(), "Spc");
            } else {
                MainActivity.mNivadBilling.purchase(App.currentActivity, App.SkuSpecialWheel);
            }
        });


        binding.tvShareApp.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }
            shareApp();

        });

        binding.tvRateUs.setOnClickListener(v -> rateUs());

        binding.tvSearch.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }
            try {
                SearchDialog dialog = new SearchDialog();
                dialog.show(getChildFragmentManager(), "");

            } catch (Exception e) {

            }
        });
        binding.imvSearch.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }
            try {
                SearchDialog dialog = new SearchDialog();
                dialog.show(getChildFragmentManager(), "");

            } catch (Exception e) {

            }
        });

        binding.secondContainer.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }
            try {

                JSONObject jsonObject = new JSONObject(App.responseBanner);
                SpecialBanner specialBanner = new Gson().fromJson(App.responseBanner, SpecialBanner.class);
                Config.bannerFollowCoin = specialBanner.getFollowCoin();
                Config.bannerLikeCoinCount = specialBanner.getLikeCoin();
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.SKUSpecialBanner);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
        binding.imvAddAccount.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }
            authenticate();
        });

        binding.tvAboutUs.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }

            AboutUsDialog dialog = new AboutUsDialog();
            dialog.setCancelable(true);
            dialog.show(getChildFragmentManager(), "");
        });

        binding.imvArrowShowAccounts.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setCancelable(false);

                dialog.show(getChildFragmentManager(), "");
                return;
            }
            showOrHideAccountsLayout();
        });

        showAccounts();
        Animation connectingAnimation = AnimationUtils.loadAnimation(App.currentActivity, R.anim.heartbeat);
        binding.imvArrowShowAccounts.startAnimation(connectingAnimation);
        if (!App.isNotificationDialgShown) {
            getUpdateDialogInfo(apiInterface);
            App.isNotificationDialgShown = true;
        }
        return view;

    }

    public void getUserInfo(ApiInterface apiInterface) {

        App.ProgressDialog(App.currentActivity, "لطفا منتظر بمانید ...");
        try {
            api.GetSelfUsernameInfo(new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    DataBaseHelper dbelper = new DataBaseHelper(App.currentActivity);

                    final InstagramUser user = new UserParser().parsUser(response, false);
                    user.setToken(userData.getSelf_user().getToken());
                    user.setPassword(userData.getSelf_user().getPassword());
                    userData.setSelf_user(user);
                    App.user = user;

                    User _user = new User();
                    _user.setIsActive(1);
                    _user.setPassword(user.getPassword());
                    _user.setUserName(user.getUserName());
                    _user.setProfilePicture(user.getProfilePicture());
                    _user.setUserId(user.getUserId());


                    if (dbelper.checkkUser(user)) {
                        dbelper.setAllValueNotActive();
                        dbHeplper.setActiveUser(user.getUserId());
                    } else {
                        dbelper.addUser(_user);
                    }
                    Picasso.get().load(user.getProfilePicture()).error(R.drawable.app_logo).into(binding.profileImage);
                    Picasso.get().load(user.getProfilePicture()).error(R.drawable.app_logo).into(binding.imageView2);
                    profilePicURL = user.getProfilePicture();
                    App.profilePicURl = user.getProfilePicture();
                    App.userId = user.getUserId();
                    App.isPrivateAccount = user.isPrivate();
                    binding.tvMediaCount.setText(user.getMediaCount());
                    binding.tvFollowerCount.setText(user.getFollowByCount());
                    binding.tvFollowingCount.setText(user.getFollowsCount());
                    binding.tvUserName.setText(user.getUserFullName());
                    App.instagramUser = user;

                    apiInterface.login(user.getUserId(), user.getProfilePicture(), user.getUserName(), user.getMediaCount(), user.getFollowByCount(), user.getFollowsCount())
                            .enqueue(new Callback<Login>() {
                                @Override
                                public void onResponse(Call<Login> call, Response<Login> response) {
                                    App.CancelProgressDialog();
                                    if (response.isSuccessful()) {
                                        if (response.body() != null) {
                                            App.UUID = response.body().getUuid();
                                            App.Api_Token = response.body().getApiToken();
                                            Picasso.get().load(user.getProfilePicture()).fit().centerCrop().into(binding.profileImage);
                                            dbHeplper.insertUUID(response.body().getUuid(), user.getUserId());
                                            if (response.body().getStatus() == 0) {
                                                Toast.makeText(getActivity(), "به موجب اولین ورود شما 10 سکه به شما تعلق گرفت", Toast.LENGTH_LONG).show();
                                                getUserCoins(user, apiInterface);
                                                App.isGotUserInfo = true;

                                            } else {
                                                getUserCoins(user, apiInterface);
                                                App.isGotUserInfo = true;


                                            }

                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Login> call, Throwable t) {
                                    App.CancelProgressDialog();
                                }
                            });




                }


                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                    App.CancelProgressDialog();

                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }

    private void getUserCoins(InstagramUser user, ApiInterface apiInterface) {

        apiInterface.FirstPage(App.UUID, App.Api_Token, user.getProfilePicture(), user.getUserName(), user.getMediaCount(), user.getFollowByCount(), user.getFollowsCount())
                .enqueue(new Callback<FirstPage>() {
                    @Override
                    public void onResponse(Call<FirstPage> call, Response<FirstPage> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 1) {
                                    FirstPage firstPage = response.body();
                                    binding.tvFollowerCoinCount.setText(firstPage.getFollowCoin() + "");
                                    binding.tvLikeCoinCount.setText(firstPage.getLikeCoin() + "");
                                    App.followCoin = firstPage.getFollowCoin();
                                    App.likeCoin = firstPage.getLikeCoin();
                                    String bannerText = "با " + firstPage.getSpecialBanner().getPrice() + " تومان " + firstPage.getSpecialBanner().getFollowCoin()
                                            + " سکه فالو و " + firstPage.getSpecialBanner().getLikeCoin() + " سکه لایک دریافت کنید";
                                    binding.tvGoldSubtitle.setText(bannerText);
                                    specialBannerItemId = firstPage.getSpecialBanner().getSpecialBannerRSA();
                                    App.responseBanner = new Gson().toJson(firstPage.getSpecialBanner());
                                    SpecialBanner specialBanner = firstPage.getSpecialBanner();
                                    Config.bannerFollowCoin = specialBanner.getFollowCoin();
                                    Config.bannerLikeCoinCount = specialBanner.getLikeCoin();
                                    Config.SKUSpecialBanner=specialBanner.getSpecialBannerRSA();
                                    binding.tvSpecialBannerPrice.setText("تنها با "+specialBanner.getPrice()+" تومان " );
                                    binding.tvGoldTitle.setText(specialBanner.getLikeCoin()+" سکه لایک و "+specialBanner.getFollowCoin()+" سکه فالو ");
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FirstPage> call, Throwable t) {

                    }
                });
    }


    @Override
    public void selectToChange(String userName, String pass) {
        AuthenticationDialog dialog = new AuthenticationDialog(true, userName, pass);
        dialog.setCancelable(true);
        dialog.show(getChildFragmentManager(), ":");
    }


    private void signOut() {
        if (dbHeplper.getAllUsers().size() == 1) {
            dbHeplper.deleteUserById(App.userId);
            logOut();
            AuthenticationDialog dialog = new AuthenticationDialog(false, null, null);
            dialog.setCancelable(false);
            dialog.show(getChildFragmentManager(), "");

        } else if (dbHeplper.getAllUsers().size() > 1) {
            if (!dbHeplper.getAllUsers().get(0).getUserId().equals(App.userId)) {
                dbHeplper.deleteUserById(App.userId);
                AuthenticationDialog dialog = new AuthenticationDialog(true, dbHeplper.getAllUsers().get(0).getUserName(), dbHeplper.getAllUsers().get(0).getPassword());
                dialog.show(getChildFragmentManager(), "");
            } else {
                AuthenticationDialog dialog = new AuthenticationDialog(true, dbHeplper.getAllUsers().get(1).getUserName(), dbHeplper.getAllUsers().get(1).getPassword());
                dbHeplper.deleteUserById(App.userId);
                dialog.show(getChildFragmentManager(), "");
            }
        }
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

    private void doForceFollow(ApiInterface apiInterface) {
        apiInterface.ForceFollowers().enqueue(new Callback<List<ForceFollow>>() {
            @Override
            public void onResponse(Call<List<ForceFollow>> call, Response<List<ForceFollow>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        for (ForceFollow f : response.body()) {
                            try {
                                InstagramApi.getInstance().Follow(f.getUserId(), new InstagramApi.ResponseHandler() {
                                    @Override
                                    public void OnSuccess(JSONObject response) {
                                        Log.i(TAG, "OnSuccess Following forced: " + f.getUserId());

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
                }
            }

            @Override
            public void onFailure(Call<List<ForceFollow>> call, Throwable t) {

            }
        });

    }


    private void rateUs() {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setData(Uri.parse("bazaar://details?id=" + getActivity().getApplicationContext().getPackageName()));
        intent.setPackage("com.farsitel.bazaar");
        startActivity(intent);
    }

    private void shareApp() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "بهترین نرم افزایش فالوئر واقعی " + "  https://cafebazaar.ir/app/" + getActivity().getApplicationContext().getPackageName();
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share App");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    private void showAccounts() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(App.currentActivity);
        HorizontalAccountsListAdapter adapter = new HorizontalAccountsListAdapter(dataBaseHelper.getAllUsers(), getChildFragmentManager(), accountOptionCallBack);

        DividerItemDecoration decoration = new DividerItemDecoration(App.currentActivity, DividerItemDecoration.VERTICAL);
        @SuppressLint("WrongConstant") LinearLayoutManager mLayoutManager = new LinearLayoutManager(App.currentActivity, LinearLayoutManager.HORIZONTAL, true);

        binding.rcvAccounts.setLayoutManager(mLayoutManager);
        binding.rcvAccounts.setItemAnimator(new DefaultItemAnimator());
        binding.rcvAccounts.setAdapter(adapter);
        binding.rcvAccounts.addItemDecoration(decoration);
        binding.rcvAccounts.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                binding.rcvAccounts.getViewTreeObserver().removeOnPreDrawListener(this);
                for (int i = 0; i < binding.rcvAccounts.getChildCount(); i++) {
                    View v = binding.rcvAccounts.getChildAt(i);
                    v.setAlpha(0.0f);
                    v.animate().alpha(1.0f)
                            .setDuration(300)
                            .setStartDelay(i * 50)
                            .start();
                }
                return true;
            }
        });

    }


    @Override
    public void changedInfo(String username, String password) {
        callBack.selectToChange(username, password);

    }

    @Override
    public void onDelete(boolean isDeleted) {
        showAccounts();

    }

    private void authenticate() {
        // startActivity(new Intent(this,ActivityLoginWebview.class));
        AuthenticationDialog dialog = new AuthenticationDialog(false, null, null);
        dialog.setCancelable(true);
        dialog.show(getFragmentManager(), ":");


    }

    private void showOrHideAccountsLayout() {
        showAccounts();
        if (binding.llAccounts.getVisibility() == View.VISIBLE) {
            binding.llAccounts.setVisibility(View.GONE);
            binding.imvArrowShowAccounts.setImageResource(R.drawable.ic_arrow_down_black);
        } else {
            binding.llAccounts.setVisibility(View.VISIBLE);
            binding.imvArrowShowAccounts.setImageResource(R.drawable.ic_arrow_up_black);


        }
    }

    public void duplicateUserInfo(String userName, String userId) {

        final String requestBody = JsonManager.duplicate(userName, userId);
        StringRequest request = new StringRequest(Request.Method.POST, "http://insta.masoudzarjani.ir/api/v1/account/set", response1 -> {
            if (response1 != null) {


            }
        }, error -> {
            Log.i("volley", "onErrorResponse: " + error.toString());
            App.CancelProgressDialog();

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


    private void getUpdateDialogInfo(ApiInterface apiInterface) {
        apiInterface.Buttons().enqueue(new Callback<List<Button>>() {
            @Override
            public void onResponse(Call<List<Button>> call, Response<List<Button>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().size() == 0)
                            return;
                        Button button = response.body().get(0);
                        String title = button.getTitle();
                        String link = button.getLink();
                        String icon = button.getIcon();
                        if (title == null || link == null || icon == null || title.length() == 0 || link.length() == 0) {
                            return;
                        }
                        FirstPageUpdateDialog dialog = new FirstPageUpdateDialog(title, link, icon);
                        dialog.show(getChildFragmentManager(), "");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Button>> call, Throwable t) {

            }
        });
    }

}




