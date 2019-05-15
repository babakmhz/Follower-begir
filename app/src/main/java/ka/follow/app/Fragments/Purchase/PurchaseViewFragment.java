package ka.follow.app.Fragments.Purchase;

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

import com.squareup.picasso.Picasso;

import ka.follow.app.App;
import ka.follow.app.Dialog.PurchasePackages.PurchaseLike;
import ka.follow.app.Dialog.SearchDialog;
import ka.follow.app.Dialog.SelectPictureDialog;
import ka.follow.app.Interface.DirectPurchaseDialogInterface;
import ka.follow.app.Interface.ImagePickerInterface;
import ka.follow.app.R;
import ka.follow.app.Retrofit.ApiClient;
import ka.follow.app.Retrofit.ApiInterface;
import ka.follow.app.Retrofit.UserCoin;
import ka.follow.app.databinding.FragmentPurchaseViewBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ka.follow.app.App.TAG;
import static ka.follow.app.Retrofit.ApiClient.retrofit;


@SuppressLint("ValidFragment")
public class PurchaseViewFragment extends Fragment implements ImagePickerInterface {


    private final DirectPurchaseDialogInterface callBackDirectPurchase;
    ImagePickerInterface callback;
    FragmentPurchaseViewBinding binding;
    private String selectedPicURL = null;
    private String itemId;
    private Dialog progressDialog;

    public PurchaseViewFragment(DirectPurchaseDialogInterface callBackDirectPurchase) {
        this.callBackDirectPurchase=callBackDirectPurchase;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_purchase_view, container, false);
        callback = this;
        View view = binding.getRoot();
        binding.tvUserName.setText(App.user.getUserName());
        Picasso.get().load(App.profilePicURl).fit().centerCrop().into(binding.imgProfileImage);

        ApiClient.getClient();

        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        binding.imvPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.isPrivateAccount) {
                    Toast.makeText(getActivity(), "اکانت شما خصوصی می باشد. لطفا اکانت خود را عمومی کرده و برنامه را مجددا راه اندازی نمایید", Toast.LENGTH_SHORT).show();
                    return;
                }
                SelectPictureDialog selectPictureDialog = new SelectPictureDialog(callback,true);
                selectPictureDialog.show(getChildFragmentManager(), ":");

            }
        });
        binding.btnConfirm.setOnClickListener(v -> {
            submitOrder(apiInterface);
        });

        binding.btnConfirmAndPay.setOnClickListener(v -> {

            PurchaseLike dialog = new PurchaseLike(3,selectedPicURL,binding.seekBar.getProgress(),itemId,callBackDirectPurchase);
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
        binding.imvPickImage.setBackgroundDrawable(getActivity().getDrawable(R.drawable.rounded_orange));
        selectedPicURL = imageURL;
        itemId = imageId;
        try {
            Picasso.get().load(imageURL).error(R.drawable.app_logo).into(binding.imvSelectedPic);
            progressDialog.cancel();
        } catch (Exception e) {
            Log.i(TAG, "selectedPic: " + e);
            progressDialog.cancel();

        }


    }

    private void submitOrder(ApiInterface apiInterface) {

        if (App.likeCoin <= 0) {
            Toast.makeText(App.currentActivity, "سکه کافی ندارید ", Toast.LENGTH_SHORT).show();

        } else if (selectedPicURL == null) {
            Toast.makeText(App.currentActivity, "یک عکس انتخاب کنید", Toast.LENGTH_SHORT).show();

        } else if (binding.seekBar.getProgress() == 0) {
            Toast.makeText(App.currentActivity, "تعداد سفارش را مشخص کنید", Toast.LENGTH_SHORT).show();
        } else {
            int count = binding.seekBar.getProgress();
            apiInterface.SetOrder(App.UUID, App.Api_Token, 3, itemId, count, count, selectedPicURL).enqueue(new Callback<UserCoin>() {
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
