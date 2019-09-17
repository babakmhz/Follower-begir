package com.follow.nobahar.Fragments.Purchase;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.follow.nobahar.Interface.DirectPurchaseDialogInterface;
import com.follow.nobahar.Interface.ImagePickerInterface;
import com.squareup.picasso.Picasso;

import com.follow.nobahar.App;
import com.follow.nobahar.Dialog.PurchasePackages.PurchaseLike;
import com.follow.nobahar.Dialog.SearchDialog;
import com.follow.nobahar.Dialog.SelectPictureDialog;

import ir.novahar.followerbegir.R;
import com.follow.nobahar.Retrofit.ApiClient;
import com.follow.nobahar.Retrofit.ApiInterface;
import com.follow.nobahar.Retrofit.UserCoin;
import ir.novahar.followerbegir.databinding.FragmentPurchaseLikeBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressLint("ValidFragment")
public class PurchaseLikeFragment extends Fragment implements ImagePickerInterface {


    private final DirectPurchaseDialogInterface callBackDirectPurchase;
    ImagePickerInterface callback;
    FragmentPurchaseLikeBinding binding;
    private String selectedPicURL = null;
    private String itemId;
    private Dialog progressDialog;

    public PurchaseLikeFragment(DirectPurchaseDialogInterface callBackDirectPurchase) {
        this.callBackDirectPurchase=callBackDirectPurchase;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_purchase_like, container, false);
        callback = this;
        View view = binding.getRoot();
        binding.tvUserName.setText(App.user.getUserName());
        Picasso.get().load(App.profilePicURl).fit().centerCrop().into(binding.imgProfileImage);


        binding.imvPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.isPrivateAccount) {
                    Toast.makeText(App.currentActivity, "اکانت شما خصوصی می باشد. لطفا اکانت خود را عمومی کرده و برنامه را مجددا راه اندازی نمایید", Toast.LENGTH_SHORT).show();
                    return;
                }
                SelectPictureDialog selectPictureDialog = new SelectPictureDialog(callback,false);
                selectPictureDialog.show(getChildFragmentManager(), ":");

            }
        });
        binding.btnConfirm.setOnClickListener(v -> {
            submitOrder();
        });

        binding.btnConfirmAndPay.setOnClickListener(v -> {

            PurchaseLike dialog = new PurchaseLike(0,selectedPicURL,binding.seekBar.getProgress(),itemId,callBackDirectPurchase);
            dialog.show(getChildFragmentManager(), "");

        });

        binding.tvOrderForOther.setOnClickListener(v->{
            SearchDialog dialog=new SearchDialog();
            dialog.show(getFragmentManager(),"");
        });
        binding.tvLikeCoinCounts.setText(App.likeCoin + "");
        binding.tvLikeExpenseCount.setText(0 + "");
        binding.tvLikeOrderCount.setText("0");
        binding.seekBar.setProgress(0);
        binding.seekBar.setMax(App.likeCoin / 2);
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


        return view;

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void selectedPic(String imageId, String imageURL) {
        ProgressDialog();
        binding.constraintLayout.setBackground(null);
        binding.imvSelectPic.setVisibility(View.INVISIBLE);
        binding.tvSelectPic.setVisibility(View.INVISIBLE);
        binding.imvPickImage.setBackgroundDrawable(App.currentActivity.getDrawable(R.drawable.rounded_orange));
        selectedPicURL = imageURL;
        itemId = imageId;
        try {
            Picasso.get().load(imageURL).error(R.drawable.app_logo).into(binding.imvSelectedPic);
            progressDialog.cancel();
        } catch (Exception e) {
            Log.i(App.TAG, "selectedPic: " + e);
            progressDialog.cancel();

        }


    }

    private void submitOrder() {

        if (App.likeCoin <= 0) {
            Toast.makeText(App.currentActivity, "سکه کافی ندارید ", Toast.LENGTH_SHORT).show();

        } else if (selectedPicURL == null) {
            Toast.makeText(App.currentActivity, "یک عکس انتخاب کنید", Toast.LENGTH_SHORT).show();

        } else if (binding.seekBar.getProgress() == 0) {
            Toast.makeText(App.currentActivity, "تعداد سفارش را مشخص کنید", Toast.LENGTH_SHORT).show();
        } else {

            ApiClient.getClient();

            final ApiInterface apiInterface = ApiClient.retrofit.create(ApiInterface.class);
            int count = binding.seekBar.getProgress();
            apiInterface.SetOrder(App.UUID, App.Api_Token, 0, itemId, count, count, selectedPicURL).enqueue(new Callback<UserCoin>() {
                @Override
                public void onResponse(Call<UserCoin> call, Response<UserCoin> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus()) {
                                App.likeCoin = response.body().getLikeCoin();
                                binding.tvLikeCoinCounts.setText(App.likeCoin + "");
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
