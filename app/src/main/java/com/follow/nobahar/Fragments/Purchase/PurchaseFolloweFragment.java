package com.follow.nobahar.Fragments.Purchase;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.follow.nobahar.App;
import com.follow.nobahar.Dialog.PurchasePackages.PurchaseLike;
import com.follow.nobahar.Dialog.SearchDialog;
import com.follow.nobahar.Interface.DirectPurchaseDialogInterface;
import com.follow.nobahar.R;
import com.follow.nobahar.Retrofit.ApiClient;
import com.follow.nobahar.Retrofit.ApiInterface;
import com.follow.nobahar.Retrofit.UserCoin;
import com.follow.nobahar.databinding.FragmentPurchaseFollowerBinding;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressLint("ValidFragment")
public class PurchaseFolloweFragment extends Fragment {
    private final DirectPurchaseDialogInterface callBackDirectPurchase;
    private View view;
    private FragmentPurchaseFollowerBinding binding;
    private String selectedPicURL;
    private String itemId;
    private Dialog progressDialog;

    public PurchaseFolloweFragment(DirectPurchaseDialogInterface callBackDirectPurchase) {
        this.callBackDirectPurchase = callBackDirectPurchase;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_purchase_follower, container, false);
        view = binding.getRoot();
        Picasso.get().load(App.profilePicURl).into(binding.imgProfileImage);
        binding.tvUserName.setText(App.user.getUserName());
        binding.tvLikeCoinCounts.setText(App.likeCoin + "");
        binding.tvFollowerCoin.setText(App.followCoin + "");

        binding.tvUserName.setText(App.user.getUserName());
        Picasso.get().load(App.profilePicURl).fit().centerCrop().into(binding.imgProfileImage);
        binding.tvLikeExpenseCount.setText(0 + "");
        binding.tvLikeOrderCount.setText("0");
        binding.seekBar.setProgress(0);
        binding.seekBar.setMax(App.followCoin / 2);
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.tvLikeOrderCount.setText(progress + "");
                binding.tvLikeExpenseCount.setText(progress * 2 + "");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        binding.btnConfirm.setOnClickListener(v -> {
            submitOrder();
        });

        binding.btnConfirmAndPay.setOnClickListener(v -> {
            PurchaseLike dialog = new PurchaseLike(1, App.profilePicURl, binding.seekBar.getProgress(), App.userId, callBackDirectPurchase);

            dialog.show(getChildFragmentManager(), "");

        });
        binding.tvOrderForOther.setOnClickListener(v->{
            SearchDialog dialog=new SearchDialog();
            dialog.show(getFragmentManager(),"");
        });

        return view;

    }


    private void submitOrder() {
        if (App.followCoin <= 0) {
            Toast.makeText(App.currentActivity, "سکه کافی ندارید ", Toast.LENGTH_SHORT).show();

        } else if (binding.seekBar.getProgress() == 0) {
            Toast.makeText(App.currentActivity, "تعداد سفارش را مشخص کنید", Toast.LENGTH_SHORT).show();
        } else {
            ApiClient.getClient();

            final ApiInterface apiInterface = ApiClient.retrofit.create(ApiInterface.class);
            int count = binding.seekBar.getProgress();
            apiInterface.SetOrder(App.UUID, App.Api_Token, 1, App.userId, count, count, App.profilePicURl).enqueue(new Callback<UserCoin>() {
                @Override
                public void onResponse(Call<UserCoin> call, Response<UserCoin> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus()) {
                                App.followCoin = response.body().getFollowCoin();
                                binding.tvFollowerCoin.setText(App.followCoin + "");
                                Toast.makeText(App.currentActivity, "سفارش شما با موفقیت ثبت شد", Toast.LENGTH_SHORT).show();
                                binding.seekBar.setProgress(0);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserCoin> call, Throwable t) {

                }
            });

        }
    }

    public void ProgressDialog() {
        progressDialog = new Dialog(App.currentActivity);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.dialog_uploading);
        progressDialog.show();
    }

}
