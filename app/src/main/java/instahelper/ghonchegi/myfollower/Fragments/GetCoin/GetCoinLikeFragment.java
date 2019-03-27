package instahelper.ghonchegi.myfollower.Fragments.GetCoin;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.FragmentGetCoinLikeBinding;
import instahelper.ghonchegi.myfollower.instaAPI.InstaApiException;
import instahelper.ghonchegi.myfollower.instaAPI.InstagramApi;

import static instahelper.ghonchegi.myfollower.App.requestQueue;


public class GetCoinLikeFragment extends Fragment {
    FragmentGetCoinLikeBinding binding;
    ArrayList<String> likedUsers = new ArrayList<>();
    Handler h = new Handler();
    int delay = 10 * 1000; //1 second=1000 milisecond, 10*1000=15seconds
    Runnable runnable;
    private View view;
    private String imageId;
    private int transactionId;
    private boolean autoLike = false;
    private Dialog progressDialog;

    public GetCoinLikeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_get_coin_like, container, false);

        view = binding.getRoot();
        binding.tvLikeCoinCount.setText("" + App.likeCoin);
        binding.btnNext.setOnClickListener(v -> {
            getLikeOrder();
        });
        binding.btnAutoLike.setOnClickListener(v -> {
            autoLike = true;
            ProgressDialog("انجام عملیات لایک خودکار");
            h.postDelayed(runnable = new Runnable() {
                public void run() {
                    binding.btnDoLike.performClick();
                    h.postDelayed(runnable, delay);
                }
            }, delay);


        });


        binding.btnReport.setOnClickListener(v -> {


        });
        binding.btnDoLike.setOnClickListener(v -> {
            try {
                likeInProgress();
                InstagramApi.getInstance().Like(imageId, new InstagramApi.ResponseHandler() {
                    @Override
                    public void OnSuccess(JSONObject response) {
                        submit();
                        likeFinished();

                    }

                    @Override
                    public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                        binding.btnNext.performClick();
                        likeFinished();

                    }
                });
            } catch (InstaApiException e) {
                e.printStackTrace();
                likeFinished();

            }
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
        final String requestBody = JsonManager.getOrders(0);
        StringRequest request = new StringRequest(Request.Method.POST, App.Base_URL + "transaction/get", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null || response.equals("")) {
                    Toast.makeText(getActivity(), "سفارش فعالی موجود نیست", Toast.LENGTH_SHORT).show();
                    binding.imvPic.setImageResource(R.drawable.ic_image_black);
                    if (autoLike) {
                        progressDialog.dismiss();
                        autoLike = false;
                        h.removeCallbacks(runnable); //stop handler
                    }
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Picasso.get().load(jsonObject.getString("image_path")).into(binding.imvPic);
                    imageId = jsonObject.getString("type_id");
                    transactionId = jsonObject.getInt("id");
                    checkLikers();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                error -> {
                    if (autoLike) {
                        progressDialog.dismiss();
                        autoLike = false;
                        h.removeCallbacks(runnable); //stop handler
                    }

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

        final String requestBody = JsonManager.setSubmit(0, transactionId);

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
                    if (autoLike) {
                        progressDialog.dismiss();
                        autoLike = false;
                        h.removeCallbacks(runnable); //stop handler
                    }
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

    public void ProgressDialog(String progressMessage) {
        autoLike = true;
        progressDialog = new Dialog(getContext());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.dialog_auto_like_follow);
        TextView txtProgressMessage = (TextView) progressDialog.findViewById(R.id.txtProgressMessage);
        Button btnStop = progressDialog.findViewById(R.id.btnCancel);
        if (progressMessage != null) {
            txtProgressMessage.setText(progressMessage);
        }
        btnStop.setOnClickListener(v -> {
            autoLike = false;
            h.removeCallbacks(runnable); //stop handler
            progressDialog.dismiss();
        });
        progressDialog.show();
    }

    private void checkLikers() {
        try {
            InstagramApi.getInstance().GetMediaLikers(imageId, new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("users");
                        likedUsers.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject temp = jsonArray.getJSONObject(i);
                            likedUsers.add(temp.getString("pk"));
                            Log.d("LastPOSITION", "LastPOSITION: " + i);

                        }
                        for (String item : likedUsers) {
                            if (App.userId.equals(item)) {
                                binding.btnNext.performClick();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {

                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }
}
