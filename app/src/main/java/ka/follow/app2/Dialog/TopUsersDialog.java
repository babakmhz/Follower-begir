package ka.follow.app2.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;

import ka.follow.app2.Adapters.TopUsersAdapter;
import ka.follow.app2.App;
import ka.follow.app2.Models.TopUsers;
import ka.follow.app2.R;
import ka.follow.app2.Retrofit.ApiClient;
import ka.follow.app2.Retrofit.ApiInterface;
import ka.follow.app2.Retrofit.Bests;
import ka.follow.app2.Retrofit.Comment;
import ka.follow.app2.Retrofit.Follow;
import ka.follow.app2.Retrofit.Like;
import ka.follow.app2.databinding.DialogTopUsersBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ka.follow.app2.Retrofit.ApiClient.retrofit;

public class TopUsersDialog extends DialogFragment {

    private DialogTopUsersBinding binding;
    private Bests bests;

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_top_users, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Picasso.get().load(App.profilePicURl).into(binding.imgProfileImage);
        binding.tvReturn.setOnClickListener(v -> dialog.dismiss());
        binding.tvUserName.setText(App.user.getUserName());
        //endregion
        ApiClient.getClient();

        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        getTopUsers(apiInterface);

        binding.btnTopLikers.setOnClickListener(v -> {
            setAdapter(0);
            setBackground(0);
        });
        binding.btnTopfollowers.setOnClickListener(v -> {
            setAdapter(1);
            setBackground(1);

        });
        binding.btnTopCommenters.setOnClickListener(v -> {
            setAdapter(2);
            setBackground(2);
        });


        return dialog;
    }


    private void setBackground(int which) {
        try{   binding.btnTopLikers.setBackground(getResources().getDrawable(R.drawable.border_orange));
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
            }}
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }


    }

    private void getTopUsers(ApiInterface apiInterface) {
        apiInterface.Bests().enqueue(new Callback<Bests>() {
            @Override
            public void onResponse(Call<Bests> call, Response<Bests> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        bests = response.body();
                    }
                }
            }

            @Override
            public void onFailure(Call<Bests> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });



    }

    private void setAdapter(int type) {
        binding.llNoItem.setVisibility(View.GONE);
        if (bests == null) {
            return;
        }
        try
        {     JSONArray jsonArray = null;
            ArrayList<TopUsers> topUserList = new ArrayList<>();
            switch (type) {
                case 0:
                    for (Like like : bests.getLike()) {
                        TopUsers topUser = new TopUsers();
                        topUser.setCount(String.valueOf(like.getLikeCount()));
                        topUser.setUserName(like.getUserName());
                        topUser.setPicUrl(like.getImagePath());
                        topUserList.add(topUser);

                    }

                    break;
                case 1:
                    for (Follow like : bests.getFollow()) {
                        TopUsers topUser = new TopUsers();
                        topUser.setCount(String.valueOf(like.getFollowCount()));
                        topUser.setUserName(like.getUserName());
                        topUser.setPicUrl(like.getImagePath());
                        topUserList.add(topUser);

                    }
                    break;
                case 2:
                    for (Comment like : bests.getComment()) {
                        TopUsers topUser = new TopUsers();
                        topUser.setCount(String.valueOf(like.getCommentCount()));
                        topUser.setUserName(like.getUserName());
                        topUser.setPicUrl(like.getImagePath());
                        topUserList.add(topUser);

                    }
                    break;
            }
            if (  topUserList.size()==0 )
            {
                binding.llNoItem.setVisibility(View.VISIBLE);
            }
            setView(topUserList, type);}
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }


    }


    private void setView(ArrayList<TopUsers> topUsers, int type) {
        try
        {        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
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
            });}
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }

    }


}
