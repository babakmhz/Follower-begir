package instahelper.ghonchegi.myfollower.Fragments.GetCoin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Interface.AddCoinMultipleAccount;
import instahelper.ghonchegi.myfollower.Manager.DataBaseHelper;
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.Models.User;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.FragmentGetCoinFollowerBinding;
import instahelper.ghonchegi.myfollower.instaAPI.InstaApiException;
import instahelper.ghonchegi.myfollower.instaAPI.InstagramApi;

import static instahelper.ghonchegi.myfollower.App.requestQueue;


@SuppressLint("ValidFragment")
public class GetCoinFolloweFragment extends Fragment {
    FragmentGetCoinFollowerBinding binding;
    ArrayList<String> likedUsers = new ArrayList<>();
    Handler h = new Handler();
    int delay = 10 * 1000; //1 second=1000 milisecond, 10*1000=15seconds
    Runnable runnable;
    private View view;
    private String userId;
    private int transactionId;
    private boolean autoLike = false;
    private Dialog progressDialog;
    private AddCoinMultipleAccount addCoinMultipleAccount;
    private int step = 0;
    private CountDownTimer cTimer = null;


    public GetCoinFolloweFragment(AddCoinMultipleAccount addCoinMultipleAccount) {
        this.addCoinMultipleAccount = addCoinMultipleAccount;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_get_coin_follower, container, false);
        view = binding.getRoot();
        getLikeOrder();
        binding.tvFollowerCoinCount.setText(App.followCoin + "");

        binding.btnNext.setOnClickListener(v -> getLikeOrder());

        binding.btnAutoFollow.setOnClickListener(v -> {
            autoLike = true;
            ProgressDialog("انجام عملیات فالو خودکار");
            h.postDelayed(runnable = new Runnable() {
                public void run() {
                    binding.btnDoFollow.performClick();
                    h.postDelayed(runnable, delay);
                }
            }, delay);


        });
        binding.btnDoFollow.setOnClickListener(b -> {
            if (userId == null)
                return;
            likeInProgress();
            try {
                InstagramApi.getInstance().Follow(userId, new InstagramApi.ResponseHandler() {
                    @Override
                    public void OnSuccess(JSONObject response) {
                        submit();
                        likeFinished();
                    }

                    @Override
                    public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                        binding.btnNext.performClick();
                        likeFinished();
                    }
                });
            } catch (InstaApiException e) {
                e.printStackTrace();
                likeFinished();
            }
        });

        binding.btnFollowWithAll.setOnClickListener(v -> {
            ProgressDialog("در حال انجام لایک با تمام اکانت ها . جهت جلوگیری از  شناسایی  توسط اینستاگرام و مسدود سازی حساب این فرآیند ممکن است زمانبر باشد ...");
            likeWithAllAccounts();
        });


        return view;

    }

    private void likeInProgress() {
        binding.btnDoFollow.setVisibility(View.INVISIBLE);
        binding.prg.setVisibility(View.VISIBLE);
    }

    private void likeFinished() {
        binding.btnDoFollow.setVisibility(View.VISIBLE);
        binding.prg.setVisibility(View.GONE);
    }

    private void getLikeOrder() {
        final String requestBody = JsonManager.getOrders(1);
        StringRequest request = new StringRequest(Request.Method.POST, App.Base_URL + "transaction/get", response -> {
            if (response == null || response.equals("")) {

                binding.imvPic.setImageResource(R.drawable.ic_user_avatar);

                if (autoLike) {
                    progressDialog.dismiss();
                    autoLike = false;
                    h.removeCallbacks(runnable); //stop handler
                }
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (!jsonObject.getBoolean("status")) {
                    userId = null;
                    transactionId = 0;
                    Picasso.get().load(R.drawable.ic_user_avatar).into(binding.imvPic);
                    getLikeOrder();
                    return;
                }
                Picasso.get().load(jsonObject.getString("image_path")).into(binding.imvPic);
                userId = jsonObject.getString("type_id");
                transactionId = jsonObject.getInt("transaction_id");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        },
                error -> {
                    if (autoLike) {
                        progressDialog.dismiss();
                        autoLike = false;
                        h.removeCallbacks(runnable); //stop handler
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {
                return requestBody == null ? null : requestBody.getBytes();
            }
        };
        request.setTag(this);
        requestQueue.add(request);


    }

    private void submit() {

        final String requestBody = JsonManager.setSubmit(1, transactionId);

        StringRequest request = new StringRequest(Request.Method.POST, App.Base_URL + "transaction/submit", response -> {
            assert response == null;
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getBoolean("status")) {
                    App.followCoin = jsonObject.getInt("follow_coin");
                    binding.tvFollowerCoinCount.setText(App.followCoin + "");
                    getLikeOrder();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        },
                error -> {
                    if (autoLike) {
                        progressDialog.dismiss();
                        autoLike = false;
                        h.removeCallbacks(runnable); //stop handler
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {
                return requestBody == null ? null : requestBody.getBytes();
            }
        };
        request.setTag(this);
        requestQueue.add(request);
    }

    public void ProgressDialog(String progressMessage) {
        autoLike = true;
        progressDialog = new Dialog(getContext());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.dialog_auto_like_follow);
        TextView txtProgressMessage = progressDialog.findViewById(R.id.txtProgressMessage);
        Button btnStop = progressDialog.findViewById(R.id.btnCancel);
        if (progressMessage != null) {
            txtProgressMessage.setText(progressMessage);
        }
        btnStop.setOnClickListener(v -> {
            autoLike = false;
            h.removeCallbacks(runnable); //stop handler
            progressDialog.dismiss();
        });
        progressDialog.show();
    }

    private void likeWithAllAccounts() {
        InstagramApi tempApi = new InstagramApi();
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext());
        if (dataBaseHelper.getAllUsers().size() == 1) {
            Toast.makeText(getContext(), "شما تنها یک حساب دارید ", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (step >= dataBaseHelper.getAllUsers().size()) { // اگر step  بزرگتر از سایز کاربرا بود یعنی تموم شده و باید با اکانت اصلی لاگین کنه
            for (User user : dataBaseHelper.getAllUsers()) {
                if (user.getIsActive() == 1) {
                    tempApi.Login(user.getUserName(), user.getPassword(), new InstagramApi.ResponseHandler() {
                        @Override
                        public void OnSuccess(JSONObject response) {
                            progressDialog.dismiss();

                        }

                        @Override
                        public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                            progressDialog.dismiss();

                        }
                    });
                }
            }
        } else if (dataBaseHelper.getAllUsers().get(step).getIsActive() == 1 && step < dataBaseHelper.getAllUsers().size()) { // وقتی به اکانت فعال میرسه و ردش میکنه
            step++;
            likeWithAllAccounts();
        } else if (dataBaseHelper.getAllUsers().get(step).getIsActive() == 1 && step == dataBaseHelper.getAllUsers().size()) {  // اگر آخرین مورد اکانت فعال بود به اون لاگین میکنه
            tempApi.Login(dataBaseHelper.getAllUsers().get(step).getUserName(), dataBaseHelper.getAllUsers().get(step).getPassword(), new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    Log.d(App.TAG, "OnSuccess: Login " + response);
                    step++;
                    likeWithAllAccounts();
                }

                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {

                }
            });
        } else { // لاگین با اکانت دیگه
            if (cTimer == null)
                cTimer = new CountDownTimer(15 * 1000 + 1000, 1000) {

                    public void onTick(long millisUntilFinished) {


                    }

                    public void onFinish() {
                        cTimer = null;
                        tempApi.Login(dataBaseHelper.getAllUsers().get(step).getUserName(), dataBaseHelper.getAllUsers().get(step).getPassword(), new InstagramApi.ResponseHandler() {
                            @Override
                            public void OnSuccess(JSONObject response) {
                                Log.d(App.TAG, "OnSuccess: Login " + response);
                                try {
                                    tempApi.Follow(userId, new InstagramApi.ResponseHandler() {
                                        @Override
                                        public void OnSuccess(JSONObject response) {
                                            addCoinMultipleAccount.addCoinMultipleAccount(1);


                                        }

                                        @Override
                                        public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {

                                        }
                                    });
                                } catch (InstaApiException e) {
                                    e.printStackTrace();
                                }
                                step++;
                                likeWithAllAccounts();
                            }

                            @Override
                            public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                                step++;
                                likeWithAllAccounts();
                            }
                        });
                    }
                }.start();

        }

    }


}
