package instahelper.ghonchegi.myfollower.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.data.InstagramUser;

import static instahelper.ghonchegi.myfollower.App.TAG;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.Item> {

    private ArrayList<InstagramUser> usersList;
    private Context context;

    public UserSearchAdapter(ArrayList<InstagramUser> usersList) {
        this.usersList = usersList;

    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        this.context = parent.getContext();
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
                Uri uri = Uri.parse("http://instagram.com/_u/" + currentUser.getUserName());
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    context.startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/" + currentUser.getUserName())));
                }
            });


        } catch (Exception e) {
            Log.d(TAG, "onBindViewHolder: " + e.toString());
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