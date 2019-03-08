package instahelper.ghonchegi.myfollower.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import instahelper.ghonchegi.myfollower.Dialog.AccountActionsDialog;
import instahelper.ghonchegi.myfollower.Interface.AccountChangerInterface;
import instahelper.ghonchegi.myfollower.Interface.AccountOptionChooserInterface;
import instahelper.ghonchegi.myfollower.Models.User;
import instahelper.ghonchegi.myfollower.R;

import static instahelper.ghonchegi.myfollower.App.TAG;

public class AccountsListAdapter extends android.support.v7.widget.RecyclerView.Adapter<AccountsListAdapter.Item> {

    private final AccountOptionChooserInterface callBack;
    FragmentManager childFragmentManager;
    private ArrayList<User> usersList;
    private Context context;

    public AccountsListAdapter(ArrayList<User> usersList, FragmentManager childFragmentManager, AccountOptionChooserInterface callBack) {
        this.usersList = usersList;
        this.childFragmentManager = childFragmentManager;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        this.context = parent.getContext();
        View row = LayoutInflater.from(context).inflate(R.layout.cell_account_management, parent, false);
        return new AccountsListAdapter.Item(row);

    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        try {
            holder.tvUserName.setText(usersList.get(position).getUserName());
            holder.tvNumber.setText(position + 1 + "");

            if (usersList.get(position).getIsActive() == 1) {
                holder.profilePic.setBorderColor(context.getResources().getColor(R.color.orangeTextColor));
                holder.profilePic.setBorderWidth(3);
            }
            Picasso.get().load(usersList.get(position).getProfilePicture()).into(holder.profilePic);

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AccountActionsDialog dialog = new AccountActionsDialog(usersList.get(position).getProfilePicture(), usersList.get(position).getUserName()
                            ,usersList.get(position).getIsActive(),callBack,usersList.get(position).getPassword());
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
        private TextView tvNumber, tvUserName;
        private CircleImageView profilePic;
        private AppCompatImageView imvLogOut;
        private LinearLayout root;

        public Item(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvRowNumber);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            profilePic = itemView.findViewById(R.id.imgProfileImage);
            imvLogOut = itemView.findViewById(R.id.imvLogOut);
            root = itemView.findViewById(R.id.layout);
        }
    }
}
