package ka.follow.app.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import ka.follow.app.Adapters.SelectPicAdapter;
import ka.follow.app.App;
import ka.follow.app.Interface.ImagePickerInterface;
import ka.follow.app.Interface.RecievedImageFromAdapterInterface;
import ka.follow.app.Models.PictureModel;
import ka.follow.app.R;
import ka.follow.app.data.InstagramMedia;
import ka.follow.app.instaAPI.InstaApiException;
import ka.follow.app.instaAPI.InstagramApi;
import ka.follow.app.parser.MediasParser;

import static ka.follow.app.App.TAG;

@SuppressLint("ValidFragment")
public class SelectPictureDialog extends DialogFragment implements RecievedImageFromAdapterInterface {
    private final ImagePickerInterface callback;
    private final boolean isWebView;
    private RecievedImageFromAdapterInterface localCallBack;
    private RecyclerView rcvPics;
    private ArrayList<PictureModel> pictureModelArrayList = new ArrayList<>();
    private InstagramApi api = InstagramApi.getInstance();
    private boolean is_more_available = false;

    @SuppressLint("ValidFragment")
    public SelectPictureDialog(ImagePickerInterface callback,boolean isWebView) {
        this.callback = callback;
        this.isWebView=isWebView;
    }

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_pic);
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //endregion
        localCallBack = this;
        setVariables(dialog);
        getPosts();

        return dialog;
    }

    private void setView() {

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        @SuppressLint("WrongConstant") LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        //decoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_vertical));
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        SelectPicAdapter adapter = new SelectPicAdapter(App.currentActivity, pictureModelArrayList, localCallBack,isWebView);

        rcvPics.setLayoutManager(layoutManager);
        rcvPics.setItemAnimator(new DefaultItemAnimator());
        rcvPics.setAdapter(adapter);
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


    }

    public void getPosts() {

        try {
            InstagramApi api = InstagramApi.getInstance();
            api.GetSelfFeed(new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    MediasParser parser = new MediasParser();
                    ArrayList<InstagramMedia> postsList = parser.parseMedias(response);
                    for (int i = 0; i < postsList.size(); i++) {
                        InstagramMedia post = postsList.get(i);

                        Log.d(TAG, "Images " + post.getPhotoId() + "  " + post.getImageUrl());

                        String url = "";
                        try {
                            url =response.getJSONArray("items").getJSONObject(i).getString("code");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pictureModelArrayList.add(new PictureModel(post.getPhotoId(), url, post.getImageUrl(), String.valueOf(post.getLikesCount())));

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

                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }


    private void fetchNextUserMedias() {

        try {
            api.GetSelfFeed(pictureModelArrayList.get(pictureModelArrayList.size() - 1).getId(),
                    new InstagramApi.ResponseHandler() {
                        @Override
                        public void OnSuccess(JSONObject response) {
                            MediasParser parser = new MediasParser();
                            ArrayList<InstagramMedia> postsList = parser.parseMedias(response);
                            for (int i = 0; i < postsList.size(); i++) {
                                InstagramMedia post = postsList.get(i);
                                Log.d(TAG, "POST URL: " + post.getMediaLink());
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

                        }
                    });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }

    private void fetchUserMediasFinish() {
        App.CancelProgressDialog();
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setView();

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "fetchUserMediasFinish: " + e.toString());
        }

    }


    private void setVariables(Dialog dialog) {
        rcvPics = dialog.findViewById(R.id.rcvSelectPic);
    }

    @Override
    public void isRecieved(String mediaID, String imageURL) {
        callback.selectedPic(mediaID, imageURL);
        dismiss();

    }
}
