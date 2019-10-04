package com.follow.irani.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.follow.irani.App;
import com.follow.irani.Dialog.TestDialog;
import com.follow.irani.R;
import com.follow.irani.data.InstagramUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.Item> {

    private ArrayList<InstagramUser> usersList;
    private Context context;
    FragmentManager childFragmentManager;

    public UserSearchAdapter(ArrayList<InstagramUser> usersList, @Nullable FragmentManager childFragmentManager) {
        this.usersList = usersList;
        this.childFragmentManager=childFragmentManager;

    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        this.context = App.currentActivity;
        View row = LayoutInflater.from(context).inflate(R.layout.cell_user_search, parent, false);
        return new UserSearchAdapter.Item(row);

    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        try {
            InstagramUser currentUser = usersList.get(position);
            holder.tvFullName.setText(currentUser.getUserFullName());
            holder.tvUserName.setText(currentUser.getUserName());
            holder.tvFollowerCount.setText(currentUser.getFollowByCount() + " فالوئر ");
            Picasso.get().load(currentUser.getProfilePicture()).into(holder.profilePic);
            holder.root.setOnClickListener(v -> {
                TestDialog dialog=new TestDialog(currentUser.getUserId());
                dialog.show(childFragmentManager,"");

//                Uri uri = Uri.parse("http://instagram.com/_u/" + currentUser.getUserName());
//                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
//
//                likeIng.setPackage("com.instagram.android");
//
//                try {
//                    context.startActivity(likeIng);
//                } catch (ActivityNotFoundException e) {
//                    context.startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("http://instagram.com/" + currentUser.getUserName())));
//                }
            });


        } catch (Exception e) {
            Log.d(App.TAG, "onBindViewHolder: " + e.toString());
        }


    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        ConstraintLayout root;
        private TextView tvFullName, tvUserName, tvFollowerCount, tvFollowingCount;
        private CircleImageView profilePic;


        public Item(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            profilePic = itemView.findViewById(R.id.imgProfileImage);
            tvFollowerCount = itemView.findViewById(R.id.tvFollowerCount);
            tvFollowingCount = itemView.findViewById(R.id.tvFollowingCount);
            root = itemView.findViewById(R.id.rootSearch);
        }
    }
}
