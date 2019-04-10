package instahelper.ghonchegi.myfollower.Fragments.Purchase;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Dialog.PurchasePackages.PurchaseLike;
import instahelper.ghonchegi.myfollower.Dialog.SearchDialog;
import instahelper.ghonchegi.myfollower.Dialog.SelectPictureDialog;
import instahelper.ghonchegi.myfollower.Interface.DirectPurchaseDialogInterface;
import instahelper.ghonchegi.myfollower.Interface.ImagePickerInterface;
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.FragmentPurchaseLikeBinding;

import static instahelper.ghonchegi.myfollower.App.Base_URL;
import static instahelper.ghonchegi.myfollower.App.TAG;
import static instahelper.ghonchegi.myfollower.App.requestQueue;


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
                    Toast.makeText(getActivity(), "اکانت شما خصوصی می باشد. لطفا اکانت خود را عمومی کرده و برنامه را مجددا راه اندازی نمایید", Toast.LENGTH_SHORT).show();
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

    private void submitOrder() {

        if (App.likeCoin <= 0) {
            Toast.makeText(getContext(), "سکه کافی ندارید ", Toast.LENGTH_SHORT).show();

        } else if (selectedPicURL == null) {
            Toast.makeText(getContext(), "یک عکس انتخاب کنید", Toast.LENGTH_SHORT).show();

        } else if (binding.seekBar.getProgress() == 0) {
            Toast.makeText(getContext(), "تعداد سفارش را مشخص کنید", Toast.LENGTH_SHORT).show();
        } else {
            final String requestBody = JsonManager.submitOrder(0, itemId, selectedPicURL, binding.seekBar.getProgress());

            StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "transaction/set", response1 -> {
                if (response1 != null) {
                    try {
                        JSONObject jsonRootObject = new JSONObject(response1);
                        if (jsonRootObject.optBoolean("status")) {
                            App.likeCoin = Integer.parseInt(jsonRootObject.getString("like_coin"));
                            binding.tvLikeCoinCounts.setText(App.likeCoin + "");
                            Toast.makeText(getContext(), "سفارش شما با موفقیت ثبت شد", Toast.LENGTH_SHORT).show();
                            binding.seekBar.setProgress(0);

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
    }

    public void ProgressDialog() {
        progressDialog = new Dialog(getContext());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.dialog_uploading);
        progressDialog.show();
    }

}
