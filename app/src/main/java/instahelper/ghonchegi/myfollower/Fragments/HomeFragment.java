package instahelper.ghonchegi.myfollower.Fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.util.HashMap;
import java.util.Map;

import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Dialog.AccountStatisticsDialog;
import instahelper.ghonchegi.myfollower.Dialog.ReviewOrdersDialog;
import instahelper.ghonchegi.myfollower.Dialog.TransferCoinDialog;
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.Manager.SharedPreferences;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.data.InstagramUser;
import instahelper.ghonchegi.myfollower.data.UserData;
import instahelper.ghonchegi.myfollower.databinding.FragmentHomeBinding;
import instahelper.ghonchegi.myfollower.instaAPI.InstaApiException;
import instahelper.ghonchegi.myfollower.instaAPI.InstagramApi;
import instahelper.ghonchegi.myfollower.parser.UserParser;

import static instahelper.ghonchegi.myfollower.App.Base_URL;
import static instahelper.ghonchegi.myfollower.App.requestQueue;

public class HomeFragment extends Fragment {


    private View view;
    private FragmentHomeBinding binding;
    private InstagramApi api = InstagramApi.getInstance();
    private UserData userData = UserData.getInstance();


    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_home, container, false);
        view = binding.getRoot();
        getUserInfo();
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
            AccountStatisticsDialog accountStatisticsDialog = new AccountStatisticsDialog();
            accountStatisticsDialog.show(getChildFragmentManager(), "");

        });


        return view;

    }

    public void getUserInfo() {

        App.ProgressDialog(getContext(), "لطفا منتظر بمانید ...");
        try {
            api.GetSelfUsernameInfo(new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    final InstagramUser user = new UserParser().parsUser(response, false);
                    user.setToken(userData.getSelf_user().getToken());
                    user.setPassword(userData.getSelf_user().getPassword());
                    userData.setSelf_user(user);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.get().load(user.getProfilePicture()).error(R.drawable.app_logo).into(binding.profileImage);
                            Picasso.get().load(user.getProfilePicture()).error(R.drawable.app_logo).into(binding.imageView2);
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

    private void getUserCoins(InstagramUser user) {
        final String requestBody = JsonManager.firstPageItems(user, getActivity());

        StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "first_page", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response1);
                    if (jsonRootObject.optInt("status") == 1) {
                        binding.tvLikeCoinCount.setText(jsonRootObject.optInt("follow_coin") + "");
                        binding.tvFollowerCoinCount.setText(jsonRootObject.optInt("like_coin") + "");
                        App.followCoin = jsonRootObject.optInt("follow_coin");
                        App.likeCoin = jsonRootObject.optInt("like_coin");

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, error -> Log.i("volley", "onErrorResponse: " + error.toString())) {
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
}

