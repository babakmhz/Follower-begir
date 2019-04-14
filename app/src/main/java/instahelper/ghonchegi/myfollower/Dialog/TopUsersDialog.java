package instahelper.ghonchegi.myfollower.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import instahelper.ghonchegi.myfollower.Adapters.TopUsersAdapter;
import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Models.TopUsers;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.DialogTopUsersBinding;

import static instahelper.ghonchegi.myfollower.App.Base_URL;
import static instahelper.ghonchegi.myfollower.App.requestQueue;

public class TopUsersDialog extends DialogFragment {

    private DialogTopUsersBinding binding;
    private JSONObject jsonObjectMain;

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_top_users, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Picasso.get().load(App.profilePicURl).into(binding.profileImage);
        binding.tvReturn.setOnClickListener(v -> dialog.dismiss());
        binding.tvUserName.setText(App.user.getUserName());
        //endregion
        getTopUsers();

        binding.btnTopLikers.setOnClickListener(v -> {
            try {
                setAdapter(0);
                setBackground(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        binding.btnTopfollowers.setOnClickListener(v -> {
            try {
                setAdapter(1);
                setBackground(1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        binding.btnTopCommenters.setOnClickListener(v -> {
            try {
                setAdapter(2);
                setBackground(2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });


        return dialog;
    }


    private void setBackground(int which) {
        binding.btnTopLikers.setBackground(getResources().getDrawable(R.drawable.border_orange));
        binding.btnTopfollowers.setBackground(getResources().getDrawable(R.drawable.border_orange));
        binding.btnTopCommenters.setBackground(getResources().getDrawable(R.drawable.border_orange));
        switch (which) {
            case 0:
                binding.btnTopLikers.setBackground(getResources().getDrawable(R.drawable.border_orange_active_pink));

                break;
            case 1:
                binding.btnTopfollowers.setBackground(getResources().getDrawable(R.drawable.border_orange_active_pink));

                break;
            case 2:
                binding.btnTopCommenters.setBackground(getResources().getDrawable(R.drawable.border_orange_active_pink));

                break;
        }

    }

    private void getTopUsers() {

        StringRequest request = new StringRequest(Request.Method.GET, Base_URL + "bests", (String response1) -> {

            if (response1 != null) {
                try {
                    jsonObjectMain = new JSONObject(response1);
                    setAdapter(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            Log.i("volley", "onErrorResponse: " + error.toString());
        });
        request.setTag(this);
        requestQueue.add(request);

    }

    private void setAdapter(int type) throws JSONException {
        try
        {
            binding.llNoItem.setVisibility(View.GONE);

            JSONArray jsonArray = null;
            ArrayList<TopUsers> topUserList = new ArrayList<>();
            switch (type) {
                case 0:
                    jsonArray = jsonObjectMain.getJSONArray("like");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        TopUsers topUser = new TopUsers();
                        topUser.setCount(String.valueOf(object.getInt("like_count")));
                        topUser.setUserName(object.getString("user_name"));
                        topUser.setPicUrl(object.getString("image_path"));
                        topUserList.add(topUser);
                    }
                    break;
                case 1:
                    jsonArray = jsonObjectMain.getJSONArray("follow");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        TopUsers topUser = new TopUsers();
                        topUser.setCount(String.valueOf(object.getInt("follow_count")));
                        topUser.setUserName(object.getString("user_name"));
                        topUser.setPicUrl(object.getString("image_path"));
                        topUserList.add(topUser);

                    }
                    break;
                case 2:
                    jsonArray = jsonObjectMain.getJSONArray("comment");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        TopUsers topUser = new TopUsers();
                        topUser.setCount(String.valueOf(object.getInt("comment_count")));
                        topUser.setUserName(object.getString("user_name"));
                        topUser.setPicUrl(object.getString("image_path"));
                        topUserList.add(topUser);

                    }
                    break;
            }
            if (  topUserList.size()==0 )
            {
                binding.llNoItem.setVisibility(View.VISIBLE);
            }
            setView(topUserList, type);
        }
        catch (Exception e)
        {

        }


    }


    private void setView(ArrayList<TopUsers> topUsers, int type) {
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        @SuppressLint("WrongConstant") LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        //decoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_vertical));
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        TopUsersAdapter adapter = new TopUsersAdapter(topUsers, type);
        binding.rcvTopsers.setLayoutManager(mLayoutManager);
        binding.rcvTopsers.setItemAnimator(new DefaultItemAnimator());
        binding.rcvTopsers.setAdapter(adapter);
        binding.rcvTopsers.addItemDecoration(decoration);
        binding.rcvTopsers.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                binding.rcvTopsers.getViewTreeObserver().removeOnPreDrawListener(this);
                for (int i = 0; i < binding.rcvTopsers.getChildCount(); i++) {
                    View v = binding.rcvTopsers.getChildAt(i);
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
