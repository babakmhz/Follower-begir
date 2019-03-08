package instahelper.ghonchegi.myfollower.Dialog;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.FragmentTransferCoinBinding;

import static instahelper.ghonchegi.myfollower.App.Base_URL;
import static instahelper.ghonchegi.myfollower.App.requestQueue;

public class TransferCoinDialog extends DialogFragment {

    private FragmentTransferCoinBinding binding;
    private String receiverUUID = null;

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_transfer_coin, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //endregion

        Picasso.get().load(App.profilePicURl).into(binding.imvProfilePic);
        binding.imvArrowLeft.setOnClickListener(v -> dismiss());

        binding.btnLikeToFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exchangeLikeToFollowerCoin();
            }
        });

        binding.btnChangeFollowerToLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exchangeFollowToLikeCoins();
            }
        });
        return dialog;
    }

    public void exchangeLikeToFollowerCoin() {
        if (App.likeCoin < Integer.parseInt(binding.edtCoinAmountLiketoFollower.getText().toString())) {
            Toast.makeText(getContext(), "عدم موجودی کافی", Toast.LENGTH_SHORT).show();
            return;
        }
        final String requestBody = JsonManager.exchangeCoins(binding.edtCoinAmountLiketoFollower.getText().toString(), 0);

        StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "transaction/convert_coins", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response1);
                    if (jsonRootObject.getBoolean("status")) {
                        App.followCoin = Integer.parseInt(jsonRootObject.getString("follow_coin"));
                        App.likeCoin = Integer.parseInt(jsonRootObject.getString("like_coin"));
                        Toast.makeText(getContext(), "تبدیل با موفیت انجام شد", Toast.LENGTH_SHORT).show();
                        binding.edtCoinAmountLiketoFollower.setText("");
                    } else {
                        Toast.makeText(getContext(), "خطا در انتقال", Toast.LENGTH_SHORT).show();
                    }


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

    public void exchangeFollowToLikeCoins() {
        if (App.followCoin < Integer.parseInt(binding.edtCoinAmountliketoFollower2.getText().toString())) {
            Toast.makeText(getContext(), "عدم موجودی کافی", Toast.LENGTH_SHORT).show();
            return;
        }
        final String requestBody = JsonManager.exchangeCoins(binding.edtCoinAmountliketoFollower2.getText().toString(), 1);

        StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "transaction/convert_coins", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response1);
                    if (jsonRootObject.getBoolean("status")) {
                        App.followCoin = Integer.parseInt(jsonRootObject.getString("follow_coin"));
                        App.likeCoin = Integer.parseInt(jsonRootObject.getString("like_coin"));
                        Toast.makeText(getContext(), "تبدیل با موفیت انجام شد", Toast.LENGTH_SHORT).show();
                        binding.edtCoinAmountliketoFollower2.setText("");

                    } else {
                        Toast.makeText(getContext(), "خطا در انتقال", Toast.LENGTH_SHORT).show();
                    }


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

    public void transferCoin() {
        
    }
}

