package ka.follow.app.Fragments;

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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ka.follow.app.Activities.MainActivity;
import ka.follow.app.Adapters.HorizontalAccountsListAdapter;
import ka.follow.app.App;
import ka.follow.app.Dialog.AboutUsDialog;
import ka.follow.app.Dialog.AccountStatisticsDialog;
import ka.follow.app.Dialog.AuthenticationDialog;
import ka.follow.app.Dialog.FirstPageNotificationDialog;
import ka.follow.app.Dialog.FirstPageUpdateDialog;
import ka.follow.app.Dialog.LuckyWheelPickerDialog;
import ka.follow.app.Dialog.ManageAccountsDialog;
import ka.follow.app.Dialog.NetworkErrorDialog;
import ka.follow.app.Dialog.ReviewOrdersDialog;
import ka.follow.app.Dialog.SearchDialog;
import ka.follow.app.Dialog.SpecialLuckyWheelPickerDialog;
import ka.follow.app.Dialog.TicketDialog;
import ka.follow.app.Dialog.TopUsersDialog;
import ka.follow.app.Dialog.TransferCoinDialog;
import ka.follow.app.Interface.AccountChangerInterface;
import ka.follow.app.Interface.AccountOptionChooserInterface;
import ka.follow.app.Interface.PurchaseInterface;
import ka.follow.app.Interface.ValueUpdaterBroadCast;
import ka.follow.app.Manager.Config;
import ka.follow.app.Manager.DataBaseHelper;
import ka.follow.app.Manager.JsonManager;
import ka.follow.app.Manager.SharedPreferences;
import ka.follow.app.Models.User;
import ka.follow.app.R;
import ka.follow.app.data.InstagramUser;
import ka.follow.app.data.UserData;
import ka.follow.app.databinding.FragmentHomeBinding;
import ka.follow.app.instaAPI.InstaApiException;
import ka.follow.app.instaAPI.InstagramApi;
import ka.follow.app.parser.UserParser;

import static android.content.Context.MODE_PRIVATE;
import static ka.follow.app.App.Base_URL;
import static ka.follow.app.App.TAG;
import static ka.follow.app.App.isNetworkAvailable;
import static ka.follow.app.App.requestQueue;

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
        shared = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        editor = shared.edit();
        callBack = this;
        accountOptionCallBack = this;

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

        getUserInfo();
        doForceFollow();

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
            if (new SharedPreferences(getActivity()).getSpecialWheel()) {
                SpecialLuckyWheelPickerDialog dialog = new SpecialLuckyWheelPickerDialog();
                dialog.show(getChildFragmentManager(), "Spc");
            } else {
                MainActivity.mNivadBilling.purchase(getActivity(), App.SkuSpecialWheel);
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
                JSONObject bannerCount = jsonObject.getJSONObject("special_banner");
                Config.SKUSpecialBanner = bannerCount.getString("special_banner_RSA");
                Config.bannerFollowCoin = bannerCount.getInt("follow_coin");
                Config.bannerLikeCoinCount = bannerCount.getInt("like_coin");
                MainActivity.mNivadBilling.purchase(getActivity(), Config.SKUSpecialBanner);


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
        Animation connectingAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.heartbeat);
        binding.imvArrowShowAccounts.startAnimation(connectingAnimation);
        if (!App.isNotificationDialgShown) {
            getUpdateDialogInfo();
            App.isNotificationDialgShown = true;
        }
        return view;

    }

    public void getUserInfo() {

        App.ProgressDialog(getContext(), "لطفا منتظر بمانید ...");
        try {
            api.GetSelfUsernameInfo(new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    DataBaseHelper dbelper = new DataBaseHelper(getActivity());

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

                    final String requestBody = JsonManager.login(user);

                    StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "user/login", response1 -> {
                        if (response1 != null) {
                            try {
                                JSONObject jsonRootObject = new JSONObject(response1);
                                App.UUID = jsonRootObject.optString("uuid");
                                App.Api_Token = jsonRootObject.optString("api_token");
                                dbHeplper.insertUUID(jsonRootObject.optString("uuid"), user.getUserId());

                                if (jsonRootObject.optInt("status") == 0) {
                                    SharedPreferences sharedPreferences = new SharedPreferences(getActivity());
                                    duplicateUserInfo(user.getUserName(), user.getUserId());
                                    Toast.makeText(getActivity(), "به موجب اولین ورود شما 10 سکه به شما تعلق گرفت", Toast.LENGTH_SHORT).show();
                                    getUserCoins(user);
                                } else if (jsonRootObject.optInt("status") == 1) {
                                    getUserCoins(user);

                                }

                                App.CancelProgressDialog();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                App.CancelProgressDialog();

                            }


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


                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                    App.CancelProgressDialog();

                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }

    private void getUserCoins(InstagramUser user) {
        final String requestBody = JsonManager.firstPageItems(user, getActivity());

        StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "first_page", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response1);
                    if (jsonRootObject.optInt("status") == 1) {
                        binding.tvFollowerCoinCount.setText(jsonRootObject.optInt("follow_coin") + "");
                        binding.tvLikeCoinCount.setText(jsonRootObject.optInt("like_coin") + "");
                        App.followCoin = jsonRootObject.optInt("follow_coin");
                        App.likeCoin = jsonRootObject.optInt("like_coin");
                        JSONObject childJson = jsonRootObject.getJSONObject("special_banner");
                        binding.tvGoldTitle.setText(childJson.getInt("follow_coin") + " سکه فالو");
                        binding.tvGoldSubtitle.setText(childJson.getInt("like_coin") + " سکه لایک");
                        specialBannerItemId = childJson.getString("special_banner_RSA");
                        binding.tvSpecialBannerPrice.setText(childJson.getInt("price") + " تومان");
                        App.responseBanner = response1;
                        if (jsonRootObject.getString("welcome") != null && !jsonRootObject.getString("welcome").equals("")) {
                            if (!App.isNotificationDialgShown) {
                                try {
                                    FirstPageNotificationDialog dialog = new FirstPageNotificationDialog(jsonRootObject.getString("welcome"));
                                    dialog.setCancelable(true);
                                    dialog.show(getChildFragmentManager(), "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }


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


    private void doForceFollow() {


        StringRequest request = new StringRequest(Request.Method.GET, Base_URL + "force_followers", response1 -> {
            if (response1 != null) {
                try {
                    JSONArray array = new JSONArray(response1);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        try {
                            InstagramApi.getInstance().Follow(jsonObject.getString("user_id"), new InstagramApi.ResponseHandler() {
                                @Override
                                public void OnSuccess(JSONObject response) {
                                    try {
                                        Log.i(TAG, "OnSuccess Following forced: " + jsonObject.getString("user_id").toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {

                                }
                            });
                        } catch (InstaApiException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, error -> {
            Log.i("volley", "onErrorResponse: " + error.toString());
        });
        request.setTag(this);
        requestQueue.add(request);
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
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        HorizontalAccountsListAdapter adapter = new HorizontalAccountsListAdapter(dataBaseHelper.getAllUsers(), getChildFragmentManager(), accountOptionCallBack);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        @SuppressLint("WrongConstant") LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);

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

    private void authenticate() {
        // startActivity(new Intent(this,ActivityLoginWebview.class));
        AuthenticationDialog dialog = new AuthenticationDialog(false, null, null);
        dialog.setCancelable(true);
        dialog.show(getFragmentManager(), ":");


    }

    private void showOrHideAccountsLayout() {
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


    private void getUpdateDialogInfo() {
        StringRequest request = new StringRequest(Request.Method.GET, Base_URL + "buttons", response1 -> {
            if (response1 != null) {
                try {
                    JSONArray jsonObject = new JSONArray(response1);
                    String title = jsonObject.getJSONObject(0).getString("title");
                    String link = jsonObject.getJSONObject(0).getString("link");
                    String icon = jsonObject.getJSONObject(0).getString("icon");
                    if (title == null || link == null || icon == null || title.length() == 0 || link.length() == 0) {
                        return;
                    }
                    FirstPageUpdateDialog dialog = new FirstPageUpdateDialog(title, link, icon);
                    dialog.show(getChildFragmentManager(), "");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            Log.i("volley", "onErrorResponse: " + error.toString());
            App.CancelProgressDialog();

        });
        request.setTag(this);
        requestQueue.add(request);
    }
}




