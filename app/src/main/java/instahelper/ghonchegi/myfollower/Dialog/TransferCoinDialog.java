package instahelper.ghonchegi.myfollower.Dialog;

import android.app.Dialog;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
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
import instahelper.ghonchegi.myfollower.Interface.ExternalAccountTransferChooserInsterface;
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.FragmentTransferCoinBinding;

import static instahelper.ghonchegi.myfollower.App.Base_URL;
import static instahelper.ghonchegi.myfollower.App.TAG;
import static instahelper.ghonchegi.myfollower.App.requestQueue;

public class TransferCoinDialog extends DialogFragment implements ExternalAccountTransferChooserInsterface {

    int type;
    private FragmentTransferCoinBinding binding;
    //Todo
    private String receiverUUID = null;
    private ExternalAccountTransferChooserInsterface callBack;

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_transfer_coin, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        callBack = this;
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

        binding.btnSelectAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountTransferChooserDialog dialog1 = new AccountTransferChooserDialog(callBack);
                dialog1.setCancelable(true);
                dialog1.show(getChildFragmentManager(), "this");
            }
        });

        binding.btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferCoin();
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
        if (TextUtils.isEmpty(binding.edtCoinAmount.getText().toString()) || binding.edtCoinAmount.getText().toString().equals("0")) {
            Toast.makeText(getContext(), "تعداد سکه را مشخص کنید", Toast.LENGTH_SHORT).show();
            return;
        }
        if (receiverUUID == null) {
            Toast.makeText(getContext(), "گیرنده سکه را مشخص کنید", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!binding.rvFollowerCoin.isChecked() && !binding.rvLikeCoin.isChecked()) {
            Toast.makeText(getContext(), "نوع انتقال را مشخص کنید ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (binding.rvLikeCoin.isChecked()) {
            type = 0;
            if (App.likeCoin < Integer.parseInt(binding.edtCoinAmount.getText().toString())) {
                Toast.makeText(getContext(), "عدم موجودی", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (binding.rvFollowerCoin.isChecked()) {
            type = 1;
            if (App.followCoin < Integer.parseInt(binding.edtCoinAmount.getText().toString())) {
                Toast.makeText(getContext(), "عدم موجودی", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        final String requestBody = JsonManager.transferCoins(binding.edtCoinAmount.getText().toString(), type, receiverUUID);

        StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "transaction/transfer_coins", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response1);
                    if (jsonRootObject.getBoolean("status")) {
                        Log.i(TAG, "transferCoin: " + response1);
                        Toast.makeText(getContext(), "انتقال با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
                        binding.edtCoinAmount.setText("");
                        if (binding.rvLikeCoin.isChecked()) {
                            App.likeCoin = jsonRootObject.getInt("like_coin");
                        } else if (binding.rvFollowerCoin.isChecked()) {
                            App.followCoin = jsonRootObject.getInt("follow_coin");
                        }
                        //follow_coin     ya like_coin

                    } else if (!jsonRootObject.getBoolean("status")) {
                        Toast.makeText(getContext(), jsonRootObject.getString("error_message"), Toast.LENGTH_SHORT).show();
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

    @Override
    public void setUUID(String UUID, String receiverProfilePic) {
        Log.d(TAG, "setUUID: " + UUID);
        if (App.profilePicURl.equals(receiverProfilePic)) {
            Toast.makeText(getContext(), "انتقال به اکانت یکسان مجاز نمی باشد", Toast.LENGTH_SHORT).show();
        } else {
            Picasso.get().load(receiverProfilePic).into(binding.imgProfileImage);
            receiverUUID = UUID;
        }
    }
}

