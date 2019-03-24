package instahelper.ghonchegi.myfollower.Fragments.Purchase;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import instahelper.ghonchegi.myfollower.Dialog.SelectPictureDialog;
import instahelper.ghonchegi.myfollower.Interface.ImagePickerInterface;
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.FragmentPurchaseFollowerBinding;

import static instahelper.ghonchegi.myfollower.App.Base_URL;
import static instahelper.ghonchegi.myfollower.App.TAG;
import static instahelper.ghonchegi.myfollower.App.requestQueue;


public class PurchaseFolloweFragment extends Fragment  {
    private View view;
    private FragmentPurchaseFollowerBinding binding;
    private String selectedPicURL;
    private String itemId;

    public PurchaseFolloweFragment() {
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_purchase_follower, container, false);
        view = binding.getRoot();
        binding.tvFollowCoin.setText(App.followCoin + "");

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

        binding.btnConfirmAndPay.setOnClickListener(v->{
            PurchaseLike dialog = new PurchaseLike(1);
            dialog.show(getChildFragmentManager(),"");

        });


        return view;

    }


    private void submitOrder() {
        if (App.followCoin <= 0) {
            Toast.makeText(getContext(), "سکه کافی ندارید ", Toast.LENGTH_SHORT).show();

        }else if (binding.seekBar.getProgress() == 0) {
            Toast.makeText(getContext(), "تعداد سفارش را مشخص کنید", Toast.LENGTH_SHORT).show();
        } else {
            final String requestBody = JsonManager.submitOrder(1, App.userId, App.profilePicURl, binding.seekBar.getProgress());

            StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "transaction/set", response1 -> {
                if (response1 != null) {
                    try {
                        JSONObject jsonRootObject = new JSONObject(response1);
                        if (jsonRootObject.optBoolean("status")) {
                            App.followCoin = Integer.parseInt(jsonRootObject.getString("follow_coin"));
                            binding.tvFollowCoin.setText(App.followCoin + "");
                            Toast.makeText(getContext(), "سفارش شما با موفقیت ثبت شد", Toast.LENGTH_SHORT).show();
                            binding.seekBar.setProgress(0);


                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    Toast.makeText(getContext(), "خطا در ثبت سفارش", Toast.LENGTH_SHORT).show();

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
}
