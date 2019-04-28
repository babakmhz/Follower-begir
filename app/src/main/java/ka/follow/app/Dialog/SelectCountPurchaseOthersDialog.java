package ka.follow.app.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import ka.follow.app.App;
import ka.follow.app.Manager.JsonManager;
import ka.follow.app.R;
import ka.follow.app.databinding.DialogSelectCountPurchaseOthersBinding;

import static ka.follow.app.App.Base_URL;
import static ka.follow.app.App.requestQueue;

@SuppressLint("ValidFragment")
public class SelectCountPurchaseOthersDialog extends DialogFragment {

    private final int type;
    private final String imageAdsress;
    private DialogSelectCountPurchaseOthersBinding binding;
    private String profilePic, userName, itemId;

    public SelectCountPurchaseOthersDialog(int type, String id, String imageAddress) {
        this.type = type;
        this.imageAdsress = imageAddress;
        this.itemId = id;
    }

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_select_count_purchase_others, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Picasso.get().load(imageAdsress).fit().centerCrop().into(binding.imvProfilePic);
        //endregion

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

        binding.btnPurchase.setOnClickListener(v -> {
            submitOrder();
        });
        return dialog;
    }

    private void submitOrder() {

        if (App.likeCoin <= 0) {
            Toast.makeText(getContext(), "سکه کافی ندارید ", Toast.LENGTH_SHORT).show();

        } else if (binding.seekBar.getProgress() == 0) {
            Toast.makeText(getContext(), "تعداد سفارش را مشخص کنید", Toast.LENGTH_SHORT).show();
        } else {
            final String requestBody = JsonManager.submitOrder(type, itemId, imageAdsress, binding.seekBar.getProgress());

            StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "transaction/set", response1 -> {
                if (response1 != null) {
                    try {
                        JSONObject jsonRootObject = new JSONObject(response1);
                        if (jsonRootObject.optBoolean("status")) {
                            App.likeCoin = Integer.parseInt(jsonRootObject.getString("like_coin"));
                            Toast.makeText(getContext(), "سفارش شما با موفقیت ثبت شد", Toast.LENGTH_SHORT).show();
                            binding.seekBar.setProgress(0);
                            dismiss();

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


}
