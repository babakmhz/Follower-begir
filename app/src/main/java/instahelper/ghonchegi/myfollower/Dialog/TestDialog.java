package instahelper.ghonchegi.myfollower.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import instahelper.ghonchegi.myfollower.Adapters.SelectPicOrderOthersAdapter;
import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Interface.RecievedImageFromAdapterInterface;
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.Models.PictureModel;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.data.InstagramMedia;
import instahelper.ghonchegi.myfollower.databinding.DialogTestBinding;
import instahelper.ghonchegi.myfollower.instaAPI.InstaApiException;
import instahelper.ghonchegi.myfollower.instaAPI.InstagramApi;
import instahelper.ghonchegi.myfollower.parser.MediasParser;

import static instahelper.ghonchegi.myfollower.App.Base_URL;
import static instahelper.ghonchegi.myfollower.App.TAG;
import static instahelper.ghonchegi.myfollower.App.requestQueue;

@SuppressLint("ValidFragment")
public class TestDialog extends DialogFragment {

    private DialogTestBinding binding;
    private RecyclerView rcvPics;
    private RecievedImageFromAdapterInterface localCallBack;

    private ArrayList<PictureModel> pictureModelArrayList = new ArrayList<>();
    private InstagramApi api = InstagramApi.getInstance();
    private boolean is_more_available = false;
    private String userId;
    private String userImageAddress = "";
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public TestDialog(String userId) {
        this.userId = userId;
    }


    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_test, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //endregion
        rcvPics = binding.rcvSelectPic;
        binding.seekBar.setProgress(0);
        binding.seekBar.setMax(App.followCoin / 2);

        try {

            InstagramApi.getInstance().GetUsernameInfo(userId, new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    try {
                        userImageAddress = response.getJSONObject("user").getString("profile_pic_url");
                        Picasso.get().load(response.getJSONObject("user").getString("profile_pic_url")).into(binding.imgProfileImage);
                        binding.tvPostCount.setText(response.getJSONObject("user").getString("media_count"));
                        binding.tvFollowerCount.setText(response.getJSONObject("user").getString("follower_count"));
                        binding.tvFollowingCount.setText(response.getJSONObject("user").getString("following_count"));
                        if (!response.getJSONObject("user").getBoolean("is_private")) {
                            getPosts();
                        } else {
                            Toast.makeText(getActivity(), "این صفحه شخصی می باشد و امکان سفارش وجود ندارد", Toast.LENGTH_SHORT).show();
                            binding.prg.setVisibility(View.GONE);

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        binding.prg.setVisibility(View.GONE);


                    }


                }

                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                    binding.prg.setVisibility(View.GONE);

                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
            binding.prg.setVisibility(View.GONE);

        }
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                binding.tvPurchaseFollowerCount.setText(binding.seekBar.getProgress() + "");

            }
        });
        binding.btnSubmitFollower.setOnClickListener(v -> {
            if (binding.btnSubmitFollower.getText().toString().equals("سفارش فالوئر")) {
                binding.layoutSeekBar.setVisibility(View.VISIBLE);
                binding.btnSubmitFollower.setText("ثبت سفارش");
            } else {

                submitFollowerOrder(binding.seekBar.getProgress());

            }
        });


        return dialog;
    }

    private void submitFollowerOrder(int count) {

        if (binding.seekBar.getProgress() == 0) {
            Toast.makeText(context, "تعداد سفارش را مشخص کنید", Toast.LENGTH_SHORT).show();
        } else if (App.followCoin <= 0) {
            Toast.makeText(context, "سکه کافی ندارید ", Toast.LENGTH_SHORT).show();

        } else {
            final String requestBody = JsonManager.submitOrder(1, userId, userImageAddress, count);

            StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "transaction/set", response1 -> {
                if (response1 != null) {
                    try {
                        JSONObject jsonRootObject = new JSONObject(response1);
                        if (jsonRootObject.optBoolean("status")) {
                            App.followCoin = Integer.parseInt(jsonRootObject.getString("follow_coin"));
                            Toast.makeText(context, "سفارش شما با موفقیت ثبت شد", Toast.LENGTH_SHORT).show();
                            binding.seekBar.setProgress(0);
                            binding.tvPurchaseFollowerCount.setText("0");
                            binding.layoutSeekBar.setVisibility(View.GONE);


                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(context, "خطا در ثبت سفارش", Toast.LENGTH_SHORT).show();

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


    private void getPosts() {

        try {
            InstagramApi api = InstagramApi.getInstance();
            api.GetUserFeed(userId, new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    MediasParser parser = new MediasParser();
                    ArrayList<InstagramMedia> postsList = parser.parseMedias(response);
                    for (int i = 0; i < postsList.size(); i++) {
                        InstagramMedia post = postsList.get(i);

                        Log.d(TAG, "Images " + post.getPhotoId() + "  " + post.getImageUrl());

                        pictureModelArrayList.add(new PictureModel(post.getPhotoId(), post.getCaption(), post.getImageUrl(), String.valueOf(post.getLikesCount())));

                    }
                    is_more_available = false;
                    try {
                        is_more_available = response.getBoolean("more_available");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        is_more_available = false;
                    }
                    if (is_more_available)
                        fetchNextUserMedias();
                    else
                        fetchUserMediasFinish();
                }

                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                    binding.prg.setVisibility(View.GONE);


                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
            binding.prg.setVisibility(View.GONE);

        }
    }


    private void fetchNextUserMedias() {

        try {
            api.GetUserFeed(userId, pictureModelArrayList.get(pictureModelArrayList.size() - 1).getId(),
                    new InstagramApi.ResponseHandler() {
                        @Override
                        public void OnSuccess(JSONObject response) {
                            MediasParser parser = new MediasParser();
                            ArrayList<InstagramMedia> postsList = parser.parseMedias(response);
                            for (int i = 0; i < postsList.size(); i++) {
                                InstagramMedia post = postsList.get(i);
                                pictureModelArrayList.add(new PictureModel(post.getPhotoId(), post.getCaption(), post.getImageUrl(), String.valueOf(post.getLikesCount())));
                            }
                            is_more_available = false;
                            try {
                                is_more_available = response.getBoolean("more_available");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                is_more_available = false;
                                binding.prg.setVisibility(View.GONE);

                            }
                            if (is_more_available)
                                fetchNextUserMedias();
                            else
                                fetchUserMediasFinish();
                        }

                        @Override
                        public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {

                        }
                    });
        } catch (InstaApiException e) {
            e.printStackTrace();
            binding.prg.setVisibility(View.GONE);

        }
    }

    private void fetchUserMediasFinish() {
        App.CancelProgressDialog();
        setView();


    }

    private void setView() {

        DividerItemDecoration decoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        @SuppressLint("WrongConstant") LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        //decoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_vertical));
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        SelectPicOrderOthersAdapter adapter = new SelectPicOrderOthersAdapter(context, pictureModelArrayList, getChildFragmentManager());

        rcvPics.setLayoutManager(layoutManager);
        rcvPics.setItemAnimator(new DefaultItemAnimator());
        rcvPics.setAdapter(adapter);
        rcvPics.addItemDecoration(decoration);
        rcvPics.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                rcvPics.getViewTreeObserver().removeOnPreDrawListener(this);
                for (int i = 0; i < rcvPics.getChildCount(); i++) {
                    View v = rcvPics.getChildAt(i);
                    v.setAlpha(0.0f);
                    v.animate().alpha(1.0f)
                            .setDuration(300)
                            .setStartDelay(i * 50)
                            .start();
                }
                return true;
            }
        });
        binding.prg.setVisibility(View.GONE);


    }


}
