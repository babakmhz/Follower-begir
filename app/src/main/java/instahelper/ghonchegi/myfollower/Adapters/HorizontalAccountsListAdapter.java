package instahelper.ghonchegi.myfollower.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import instahelper.ghonchegi.myfollower.Dialog.AccountActionsDialog;
import instahelper.ghonchegi.myfollower.Interface.AccountOptionChooserInterface;
import instahelper.ghonchegi.myfollower.Models.User;
import instahelper.ghonchegi.myfollower.R;

import static instahelper.ghonchegi.myfollower.App.TAG;

public class HorizontalAccountsListAdapter extends RecyclerView.Adapter<HorizontalAccountsListAdapter.Item> {

    private final AccountOptionChooserInterface callBack;
    FragmentManager childFragmentManager;
    private ArrayList<User> usersList;
    private Context context;

    public HorizontalAccountsListAdapter(ArrayList<User> usersList, FragmentManager childFragmentManager, AccountOptionChooserInterface callBack) {
        this.usersList = usersList;
        this.childFragmentManager = childFragmentManager;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        this.context = parent.getContext();
        View row = LayoutInflater.from(context).inflate(R.layout.cell_account_management_horizontal, parent, false);
        return new HorizontalAccountsListAdapter.Item(row);

    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        try {
            holder.tvUserName.setText(usersList.get(position).getUserName());

            if (usersList.get(position).getIsActive() == 1) {
                holder.profilePic.setBorderColor(context.getResources().getColor(R.color.orangeTextColor));
                holder.profilePic.setBorderWidth(3);
            }
            Picasso.get().load(usersList.get(position).getProfilePicture()).into(holder.profilePic);
            Log.i(TAG, "onBindViewHolder: " + usersList.get(position).getUuid());
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AccountActionsDialog dialog = new AccountActionsDialog(usersList.get(position).getProfilePicture(), usersList.get(position).getUserName()
                            , usersList.get(position).getIsActive(), callBack, usersList.get(position).getPassword());
                    dialog.setCancelable(true);
                    dialog.show(childFragmentManager, "");
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
        private TextView  tvUserName;
        private CircleImageView profilePic;
        private LinearLayout root;

        public Item(@NonNull View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            profilePic = itemView.findViewById(R.id.imgProfileImage);
            root = itemView.findViewById(R.id.layout);
        }
    }
}
