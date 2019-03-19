package instahelper.ghonchegi.myfollower.Fragments;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Dialog.AccountStatisticsDialog;
import instahelper.ghonchegi.myfollower.Dialog.InstagramAutenticationDialog;
import instahelper.ghonchegi.myfollower.Dialog.LuckyWheelPickerDialog;
import instahelper.ghonchegi.myfollower.Dialog.ManageAccountsDialog;
import instahelper.ghonchegi.myfollower.Dialog.ReviewOrdersDialog;
import instahelper.ghonchegi.myfollower.Dialog.TopUsersDialog;
import instahelper.ghonchegi.myfollower.Dialog.TransferCoinDialog;
import instahelper.ghonchegi.myfollower.Interface.AccountChangerInterface;
import instahelper.ghonchegi.myfollower.Manager.DataBaseHelper;
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.Manager.SharedPreferences;
import instahelper.ghonchegi.myfollower.Models.User;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.data.InstagramUser;
import instahelper.ghonchegi.myfollower.data.UserData;
import instahelper.ghonchegi.myfollower.databinding.FragmentHomeBinding;
import instahelper.ghonchegi.myfollower.instaAPI.InstaApiException;
import instahelper.ghonchegi.myfollower.instaAPI.InstagramApi;
import instahelper.ghonchegi.myfollower.parser.UserParser;

import static android.content.Context.MODE_PRIVATE;
import static instahelper.ghonchegi.myfollower.App.Base_URL;
import static instahelper.ghonchegi.myfollower.App.requestQueue;

public class HomeFragment extends Fragment implements AccountChangerInterface {


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

    public HomeFragment() {
    }


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

        try {
            dbHeplper.createDatabase();
            dbHeplper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        db = dbHeplper.getWritableDatabase();


        getUserInfo();

        binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LuckyWheelPickerDialog dialog = new LuckyWheelPickerDialog();
                dialog.show(getChildFragmentManager(), "");
            }
        });

        /*SelectPictureDialog selectPictureDialog = new SelectPictureDialog();
        selectPictureDialog.show(getChildFragmentManager(), ":");*/
        binding.tvTransferCoin.setOnClickListener(v -> {
            TransferCoinDialog transferCoinDialog = new TransferCoinDialog();
            transferCoinDialog.show(getChildFragmentManager(), "");
        });

        binding.tvOrders.setOnClickListener(v -> {
            ReviewOrdersDialog transferCoinDialog = new ReviewOrdersDialog();
            transferCoinDialog.show(getChildFragmentManager(), "");
        });

        binding.tvAccountInfo.setOnClickListener(v -> {

            AccountStatisticsDialog accountStatisticsDialog = new AccountStatisticsDialog(profilePicURL);
            accountStatisticsDialog.show(getChildFragmentManager(), "");


        });

        binding.tvManageAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageAccountsDialog dialog = new ManageAccountsDialog(callBack);
                dialog.show(getChildFragmentManager(), "");
            }
        });

        binding.tvTopUsers.setOnClickListener(v -> {
            TopUsersDialog topUsersDialog = new TopUsersDialog();
            topUsersDialog.show(getChildFragmentManager(), "");
        });

        binding.imvLogOut.setOnClickListener(v -> {
            signOut();
        });


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
                        binding.tvGoldTitle.setText(childJson.getInt("follow_coin")+" سکه فالو");
                        binding.tvGoldSubtitle.setText(childJson.getInt("like_coin")+" سکه لایک");
                        specialBannerItemId= childJson.getString("RSA");
                        binding.tvSpecialBannerPrice.setText(childJson.getInt("price")+" تومان");


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
        InstagramAutenticationDialog dialog = new InstagramAutenticationDialog(true, userName, pass);
        dialog.setCancelable(true);
        dialog.show(getChildFragmentManager(), ":");
    }


    private void signOut() {
        if (dbHeplper.getAllUsers().size() == 1) {
            dbHeplper.deleteUserById(App.userId);
            logOut();
            InstagramAutenticationDialog dialog = new InstagramAutenticationDialog(false, null, null);
            dialog.setCancelable(false);
            dialog.show(getChildFragmentManager(), "");

        } else if (dbHeplper.getAllUsers().size() > 1) {
            if (!dbHeplper.getAllUsers().get(0).getUserId().equals(App.userId)) {
                dbHeplper.deleteUserById(App.userId);
                InstagramAutenticationDialog dialog = new InstagramAutenticationDialog(true, dbHeplper.getAllUsers().get(0).getUserName(), dbHeplper.getAllUsers().get(0).getPassword());
                dialog.show(getChildFragmentManager(), "");
            } else {
                InstagramAutenticationDialog dialog = new InstagramAutenticationDialog(true, dbHeplper.getAllUsers().get(1).getUserName(), dbHeplper.getAllUsers().get(1).getPassword());
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

}

