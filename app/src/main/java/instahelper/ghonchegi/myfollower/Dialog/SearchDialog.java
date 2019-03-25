package instahelper.ghonchegi.myfollower.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import instahelper.ghonchegi.myfollower.Adapters.UserSearchAdapter;
import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.data.InstagramUser;
import instahelper.ghonchegi.myfollower.databinding.DialogSearchBinding;
import instahelper.ghonchegi.myfollower.instaAPI.InstaApiException;
import instahelper.ghonchegi.myfollower.instaAPI.InstagramApi;

@SuppressLint("ValidFragment")
public class SearchDialog extends DialogFragment {

    DialogSearchBinding binding;


    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_search, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //endregion

        binding.imvSearch.setOnClickListener(v -> {
            search();
        });

        binding.edtSearch.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                search();
                return true;
            }
            return false;
        });
        Picasso.get().load(App.profilePicURl).into(binding.imgProfileImage);
        binding.imvArrowLeft.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    private void search() {
        try {
            InstagramApi.getInstance().SearchUsers(binding.edtSearch.getText().toString(), new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {

                    JSONArray array = null;
                    ArrayList<InstagramUser> users = new ArrayList<>();
                    try {
                        array = response.getJSONArray("users");
                        for (int i = 0; i < array.length(); i++) {
                            InstagramUser user = new InstagramUser();
                            JSONObject jsonObject = array.getJSONObject(i);
                            user.setUserFullName(jsonObject.getString("full_name"));
                            user.setUserName(jsonObject.getString("username"));
                            user.setUserId(jsonObject.getString("pk"));
                            user.setFollowByCount(jsonObject.getString("follower_count"));
                            user.setProfilePicture(jsonObject.getString("profile_pic_url"));
                            users.add(user);
                        }
                        setViewUsers(users);
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

    private void setViewUsers(ArrayList<InstagramUser> users) {
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        UserSearchAdapter adapter = new UserSearchAdapter(users);
        binding.rcvUserSearch.setLayoutManager(mLayoutManager);
        binding.rcvUserSearch.setItemAnimator(new DefaultItemAnimator());
        binding.rcvUserSearch.setAdapter(adapter);
        binding.rcvUserSearch.addItemDecoration(decoration);
        binding.rcvUserSearch.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                binding.rcvUserSearch.getViewTreeObserver().removeOnPreDrawListener(this);
                for (int i = 0; i < binding.rcvUserSearch.getChildCount(); i++) {
                    View v = binding.rcvUserSearch.getChildAt(i);
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


}
