package ka.follow.app2.Fragments.GetCoin;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import ka.follow.app2.App;
import ka.follow.app2.Interface.AddCoinMultipleAccount;
import ka.follow.app2.Manager.Config;
import ka.follow.app2.Manager.DataBaseHelper;
import ka.follow.app2.Models.User;
import ka.follow.app2.R;
import ka.follow.app2.Retrofit.ApiClient;
import ka.follow.app2.Retrofit.ApiInterface;
import ka.follow.app2.Retrofit.SimpleResult;
import ka.follow.app2.Retrofit.Transaction;
import ka.follow.app2.Retrofit.UserCoin;
import ka.follow.app2.databinding.FragmentGetCoinLikeBinding;
import ka.follow.app2.instaAPI.InstaApiException;
import ka.follow.app2.instaAPI.InstagramApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ka.follow.app2.Retrofit.ApiClient.retrofit;


@SuppressLint("ValidFragment")
public class GetCoinLikeFragment extends Fragment {
    FragmentGetCoinLikeBinding binding;
    ArrayList<String> likedUsers = new ArrayList<>();
    Handler h = new Handler();
    int delay = 10 * 1000; //1 second=1000 milisecond, 10*1000=15seconds
    Runnable runnable;
    int step = 0;
    private AddCoinMultipleAccount addCoinMultipleAccount;
    private View view;
    private String imageId;
    private int transactionId;
    private boolean autoLike = false;
    private Dialog progressDialog;
    private CountDownTimer cTimer = null;
    private boolean isAvailable = false;
    private Handler handlerCheckCoin;
    private Runnable runnableCheckCoin;
    private int retryCount = 0;

    public GetCoinLikeFragment(AddCoinMultipleAccount addCoinMultipleAccount) {
        this.addCoinMultipleAccount = addCoinMultipleAccount;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_get_coin_like, container, false);


        binding.tvUserName.setText(App.user.getUserName());
        Picasso.get().load(App.profilePicURl).fit().centerCrop().into(binding.imgProfileImage);
        handlerCheckCoin = new Handler();

        view = binding.getRoot();
        binding.tvLikeCoinCounts.setText("" + App.likeCoin);
        binding.btnNext.setOnClickListener(v -> {
            getLikeOrder();
        });
        binding.btnAutoLike.setOnClickListener(v -> {
            if (!isAvailable) {
                return;
            }

            autoLike = true;
            ProgressDialog("انجام عملیات لایک خودکار");
            h.postDelayed(runnable = new Runnable() {
                public void run() {
                    binding.btnDoLike.performClick();
                    h.postDelayed(runnable, delay);
                }
            }, delay);


        });


        binding.btnReport.setOnClickListener(v -> {
            report();


        });
        binding.btnDoLike.setOnClickListener(v -> {
            if (!isAvailable) {
                return;
            }
            try {
                likeInProgress();
                InstagramApi.getInstance().Like(imageId, new InstagramApi.ResponseHandler() {
                    @Override
                    public void OnSuccess(JSONObject response) {
                        binding.imvPic.setImageResource(R.drawable.ic_image_black);
                        submit();
                        likeFinished();

                    }

                    @Override
                    public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                        binding.imvPic.setImageResource(R.drawable.ic_image_black);
                        submit();
                        likeFinished();

                    }
                });
            } catch (InstaApiException e) {
                e.printStackTrace();
                likeFinished();

            }
        });

        binding.btnConfirmAndPay.setOnClickListener(v -> {
            if (!isAvailable) {
                return;
            }
            ProgressDialog("در حال انجام لایک با تمام اکانت ها . جهت جلوگیری از  شناسایی  توسط اینستاگرام و مسدود سازی حساب این فرآیند ممکن است زمانبر باشد ...");
            likeWithAllAccounts();
        });

        getLikeOrder();

        return view;

    }


    private void likeInProgress() {
        binding.btnDoLike.setVisibility(View.INVISIBLE);
        binding.prg.setVisibility(View.VISIBLE);
    }

    private void likeFinished() {
        binding.btnDoLike.setVisibility(View.VISIBLE);
        binding.prg.setVisibility(View.GONE);
    }

    private void report() {
        ApiClient.getClient();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        String title = "گزارش محتوای نادرست";
        String message = "پست شماره : " + transactionId;
        apiInterface.Report(App.UUID, App.Api_Token, message, title).enqueue(new Callback<SimpleResult>() {
            @Override
            public void onResponse(Call<SimpleResult> call, Response<SimpleResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        Toast.makeText(App.currentActivity, "با تشکر از گزارش شما", Toast.LENGTH_SHORT).show();
                        getLikeOrder();
                    }
                }
            }

            @Override
            public void onFailure(Call<SimpleResult> call, Throwable t) {

            }
        });
    }

    private void submit() {
        ApiClient.getClient();

        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        apiInterface.SubmitOrder(App.UUID, App.Api_Token, 0, String.valueOf(transactionId)).enqueue(new Callback<UserCoin>() {
            @Override
            public void onResponse(Call<UserCoin> call, Response<UserCoin> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getStatus()) {
                            App.likeCoin = response.body().getLikeCoin();
                            binding.tvLikeCoinCount.setText(App.likeCoin + "");
                            transactionId = 0;
                            binding.btnNext.performClick();

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserCoin> call, Throwable t) {
                if (autoLike) {
                    progressDialog.dismiss();
                    autoLike = false;
                    h.removeCallbacks(runnable);
                }
            }
        });


    }


    private void getLikeOrder() {
        if (retryCount == 5) {
            handlerCheckCoin.removeCallbacks(runnableCheckCoin);
            Toast.makeText(App.currentActivity, "سفارشی موجود نیست", Toast.LENGTH_SHORT).show();
            retryCount = 0;
            binding.imvPic.setImageResource(R.drawable.ic_image_black);
            return;

        }
        ApiClient.getClient();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        apiInterface.getOrder(App.UUID, App.Api_Token, 0).enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                if (response.code() == 204) {
                    Toast.makeText(App.currentActivity, "موردی موجود نیست", Toast.LENGTH_SHORT).show();
                    handlerCheckCoin.removeCallbacks(runnableCheckCoin);
                    return;
                }

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getStatus()) {
                            Picasso.get().load(response.body().getImagePath()).into(binding.imvPic);
                            imageId = response.body().getTypeId();
                            transactionId = response.body().getTransactionId();
                            isAvailable = true;
                            binding.btnDoLike.setEnabled(true);
                            binding.btnAutoLike.setAlpha(1f);
                            retryCount = 0;
                            binding.btnDoLike.setAlpha(1f);
                            binding.btnConfirmAndPay.setAlpha(1f);
                            handlerCheckCoin.removeCallbacks(runnableCheckCoin);


                        } else {
                            isAvailable = false;
                            binding.btnAutoLike.setAlpha(0.5f);
                            binding.btnDoLike.setAlpha(0.5f);
                            binding.btnConfirmAndPay.setAlpha(0.5f);
                            final Handler handler = new Handler();
                            retryCount++;
                            handlerCheckCoin.postDelayed(runnableCheckCoin = new Runnable() {  /// TODO HAndler that checks coins
                                public void run() {
                                    getLikeOrder();
                                }
                            }, Config.delayGetOrder);

                            return;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                isAvailable = false;
                binding.btnAutoLike.setAlpha(0.5f);
                binding.btnDoLike.setAlpha(0.5f);
                binding.btnConfirmAndPay.setAlpha(0.5f);
                binding.imvPic.setImageResource(R.drawable.ic_image_black);
                if (autoLike) {
                    progressDialog.dismiss();
                    autoLike = false;
                    h.removeCallbacks(runnable); //stop handler
                }
            }
        });

    }

    public void ProgressDialog(String progressMessage) {
        autoLike = true;
        progressDialog = new Dialog(App.currentActivity);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.dialog_auto_like_follow);
        TextView txtProgressMessage = (TextView) progressDialog.findViewById(R.id.txtProgressMessage);
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
        DataBaseHelper dataBaseHelper = new DataBaseHelper(App.currentActivity);
        if (dataBaseHelper.getAllUsers().size() == 1) {
            Toast.makeText(App.currentActivity, "شما تنها یک حساب دارید ", Toast.LENGTH_SHORT).show();
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
                                    tempApi.Like(imageId, new InstagramApi.ResponseHandler() {////// لایک با اکانت دیگه
                                        @Override
                                        public void OnSuccess(JSONObject response) {
                                            addCoinMultipleAccount.addCoinMultipleAccount(1);
                                            step++;
                                            likeWithAllAccounts();

                                        }

                                        @Override
                                        public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                                            step++;
                                            likeWithAllAccounts();

                                        }
                                    });
                                } catch (InstaApiException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                                try {
                                    if (errorResponse.getString("error_type").contains("checkpoint_challenge_required")) {
                                        Toast.makeText(App.currentActivity, "حساب شما به محدودیت رسید. دقایقی دیگر مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
                                        loginWithMainAccount();
                                        progressDialog.cancel();
                                    }
                                } catch (Exception e) {

                                }

                            }
                        });
                    }
                }.start();

        }

    }

    private void loginWithMainAccount() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(App.currentActivity);
        for (User user : dataBaseHelper.getAllUsers()) {
            if (user.getIsActive() == 1) {
                InstagramApi.getInstance().Login(user.getUserName(), user.getPassword(), new InstagramApi.ResponseHandler() {
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
    }

    @Override
    public void onStop() {
        super.onStop();
        handlerCheckCoin.removeCallbacks(runnableCheckCoin);
    }
}
