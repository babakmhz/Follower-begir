package ir.novahar.followerbegir.Fragments.GetCoin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import ir.novahar.followerbegir.App;
import ir.novahar.followerbegir.Dialog.WebViewDialog;
import ir.novahar.followerbegir.Interface.WebViewLoadedInterface;
import ir.novahar.followerbegir.Manager.Config;
import ir.novahar.followerbegir.R;
import ir.novahar.followerbegir.Retrofit.ApiClient;
import ir.novahar.followerbegir.Retrofit.ApiInterface;
import ir.novahar.followerbegir.Retrofit.SimpleResult;
import ir.novahar.followerbegir.Retrofit.Transaction;
import ir.novahar.followerbegir.Retrofit.UserCoin;
import ir.novahar.followerbegir.databinding.FragmentGetCoinViewBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.novahar.followerbegir.Retrofit.ApiClient.retrofit;


@SuppressLint("ValidFragment")
public class GetCoinViewFragment extends Fragment implements WebViewLoadedInterface {
    FragmentGetCoinViewBinding binding;

    int step = 0;

    private View view;
    private String imageId;
    private int transactionId;
    private WebViewLoadedInterface callBackWebView;
    private Handler handlerCheckCoin;
    private Runnable runnableCheckCoin;
    private boolean autoLike, isAvailable;
    private int retryCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_get_coin_view, container, false);

        binding.tvUserName.setText(App.user.getUserName());
        Picasso.get().load(App.profilePicURl).fit().centerCrop().into(binding.imgProfileImage);

        callBackWebView = this;
        view = binding.getRoot();
        binding.tvLikeCoinCount.setText("" + App.likeCoin);
        handlerCheckCoin = new Handler();
        binding.btnNext.setOnClickListener(v -> {
            getLikeOrder();
        });


        binding.btnReport.setOnClickListener(v -> {
            report();

        });
        binding.btnDoLike.setOnClickListener(v -> {
            if (transactionId == 0)
                return;
            WebViewDialog webViewDialog = new WebViewDialog(imageId, transactionId, callBackWebView);
            webViewDialog.setCancelable(false);
            webViewDialog.show(getChildFragmentManager(), "this");
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

        apiInterface.SubmitOrder(App.UUID, App.Api_Token, 3, String.valueOf(transactionId)).enqueue(new Callback<UserCoin>() {
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

        apiInterface.getOrder(App.UUID, App.Api_Token, 3).enqueue(new Callback<Transaction>() {
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
                            binding.btnDoLike.setAlpha(1f);
                            binding.btnConfirmAndPay.setAlpha(1f);
                            retryCount = 0;
                            handlerCheckCoin.removeCallbacks(runnableCheckCoin);


                        } else {
                            isAvailable = false;
                            binding.btnAutoLike.setAlpha(0.5f);
                            binding.btnDoLike.setAlpha(0.5f);
                            binding.btnConfirmAndPay.setAlpha(0.5f);
                            retryCount++;
                            final Handler handler = new Handler();
                            handlerCheckCoin.postDelayed(runnableCheckCoin = new Runnable() {  /// TODO HAndler that checks coins
                                public void run() {
                                    getLikeOrder();
                                }
                            }, Config.delayGetOrder);

                            return;
                        }
                    }
                } else {
                    isAvailable = false;
                    binding.btnDoLike.setEnabled(false);
                    binding.btnAutoLike.setAlpha(0.5f);
                    binding.btnDoLike.setAlpha(0.5f);
                    binding.btnConfirmAndPay.setAlpha(0.5f);
                    binding.imvPic.setImageResource(R.drawable.ic_image_black);


                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                isAvailable = false;
                binding.btnAutoLike.setAlpha(0.5f);
                binding.btnDoLike.setAlpha(0.5f);
                binding.btnConfirmAndPay.setAlpha(0.5f);
                binding.imvPic.setImageResource(R.drawable.ic_image_black);

            }
        });

    }


    @Override
    public void webViewOpened() {
        submit();
    }

    @Override
    public void onStop() {
        super.onStop();
        handlerCheckCoin.removeCallbacks(runnableCheckCoin);
    }

}
