package ka.follow.app.Fragments.GetCoin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import ka.follow.app.App;
import ka.follow.app.Dialog.WebViewDialog;
import ka.follow.app.Interface.WebViewLoadedInterface;
import ka.follow.app.Manager.JsonManager;
import ka.follow.app.R;
import ka.follow.app.databinding.FragmentGetCoinViewBinding;

import static ka.follow.app.App.requestQueue;


@SuppressLint("ValidFragment")
public class GetCoinViewFragment extends Fragment implements WebViewLoadedInterface {
    FragmentGetCoinViewBinding binding;

    int step = 0;

    private View view;
    private String imageId;
    private int transactionId;
    private WebViewLoadedInterface callBackWebView;

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
        binding.btnNext.setOnClickListener(v -> {
            getLikeOrder();
        });


        binding.btnReport.setOnClickListener(v -> {
            report();

        });
        binding.btnDoLike.setOnClickListener(v -> {
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

    private void getLikeOrder() {
        final String requestBody = JsonManager.getOrders(3);
        StringRequest request = new StringRequest(Request.Method.POST, App.Base_URL + "transaction/get", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null || response.equals("")) {
                    Toast.makeText(getActivity(), "سفارش فعالی موجود نیست", Toast.LENGTH_SHORT).show();
                    binding.imvPic.setImageResource(R.drawable.ic_image_black);

                    return;
                }
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("status")) {
                        getLikeOrder();
                        return;
                    }
                    Picasso.get().load(jsonObject.getString("image_path")).into(binding.imvPic);
                    imageId = jsonObject.getString("type_id");
                    transactionId = jsonObject.getInt("transaction_id");
                    // checkLikers();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                error -> {


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

    private void submit() {

        final String requestBody = JsonManager.setSubmit(3, transactionId);

        StringRequest request = new StringRequest(Request.Method.POST, App.Base_URL + "transaction/submit", response -> {
            assert response == null;
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getBoolean("status")) {
                    App.likeCoin = jsonObject.getInt("like_coin");
                    binding.tvLikeCoinCount.setText(App.likeCoin + "");
                    getLikeOrder();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        },
                error -> {

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
    public void webViewOpened() {
        submit();
    }

    private void report() {

        final String requestBody = JsonManager.report(transactionId);

        StringRequest request = new StringRequest(Request.Method.POST, App.Base_URL + "message/report/set", response -> {
            assert response == null;
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getBoolean("status")) {
                    Toast.makeText(getContext(), "با تشکر از گزارش شما", Toast.LENGTH_SHORT).show();
                    getLikeOrder();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        },
                error -> {
                    Toast.makeText(getContext(), "خطا", Toast.LENGTH_SHORT).show();

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
