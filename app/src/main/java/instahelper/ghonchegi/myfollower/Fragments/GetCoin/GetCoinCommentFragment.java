package instahelper.ghonchegi.myfollower.Fragments.GetCoin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

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
import instahelper.ghonchegi.myfollower.databinding.FragmentGetCoinCommentBinding;
import instahelper.ghonchegi.myfollower.instaAPI.InstaApiException;
import instahelper.ghonchegi.myfollower.instaAPI.InstagramApi;

import static instahelper.ghonchegi.myfollower.App.requestQueue;


public class GetCoinCommentFragment extends Fragment {
    FragmentGetCoinCommentBinding binding;
    ArrayList<String> likedUsers = new ArrayList<>();
    Handler h = new Handler();
    int delay = 10 * 1000; //1 second=1000 milisecond, 10*1000=15seconds
    Runnable runnable;
    private View view;
    private String imageId;
    private int transactionId;
    private boolean autoLike = false;
    private Dialog progressDialog;

    public GetCoinCommentFragment() {
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_get_coin_comment, container, false);
        view = binding.getRoot();
        getCommentOrders();
        binding.tvLikeCoin.setText(App.likeCoin + "");
        binding.btnDoLike.setOnClickListener(v -> {
            String commentText = "";
            if (!TextUtils.isEmpty(binding.edtCommentText.getText().toString())) {
                commentText = binding.edtCommentText.getText().toString();
            } else commentText = "بسیار عالی";

            try {
                InstagramApi.getInstance().Comment(imageId, commentText, new InstagramApi.ResponseHandler() {
                    @Override
                    public void OnSuccess(JSONObject response) {
                        submit();
                    }

                    @Override
                    public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                        binding.btnNext.performClick();
                    }
                });
            } catch (InstaApiException e) {
                e.printStackTrace();
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            getCommentOrders();
        });
        binding.btnAutoLike.setOnClickListener(v -> {
            autoLike = true;
            ProgressDialog("انجام عملیات فالو خودکار");
            h.postDelayed(runnable = new Runnable() {
                public void run() {
                    binding.btnDoLike.performClick();
                    h.postDelayed(runnable, delay);
                }
            }, delay);


        });

        return view;

    }

    private void getCommentOrders() {
        final String requestBody = JsonManager.getOrders(2);
        StringRequest request = new StringRequest(Request.Method.POST, App.Base_URL + "transaction/get", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    if (response == null || response.equals("")) {

                        binding.imvPic.setImageResource(R.drawable.ic_image_black);
                        if (autoLike) {
                            progressDialog.dismiss();
                            autoLike = false;
                            h.removeCallbacks(runnable); //stop handler
                        }
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    Picasso.get().load(jsonObject.getString("image_path")).into(binding.imvPic);
                    imageId = jsonObject.getString("type_id");
                    transactionId = jsonObject.getInt("id");


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
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {
                return requestBody == null ? null : requestBody.getBytes();
            }
        };
        request.setTag(this);
        requestQueue.add(request);


    }

    private void submit() {

        final String requestBody = JsonManager.setSubmit(2, transactionId);

        StringRequest request = new StringRequest(Request.Method.POST, App.Base_URL + "transaction/submit", response -> {
            assert response == null;
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getBoolean("status")) {
                    App.likeCoin = jsonObject.getInt("like_coin");
                    binding.tvLikeCoin.setText(App.likeCoin + "");

                    getCommentOrders();
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
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {
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
        TextView txtProgressMessage = progressDialog.findViewById(R.id.txtProgressMessage);
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

    @Override
    public void onStop() {
        super.onStop();
    }
}
