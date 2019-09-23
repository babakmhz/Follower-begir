package com.follow.nobahar.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.follow.nobahar.Interface.AccountOptionChooserInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import com.follow.nobahar.App;
import com.follow.nobahar.Dialog.AccountActionsDialog;
import com.follow.nobahar.Models.User;

import com.follow.nobahar.R;

public class AccountsListAdapter extends RecyclerView.Adapter<AccountsListAdapter.Item> {

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
        this.context = App.currentActivity;
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
            Log.i(App.TAG, "onBindViewHolder: " + usersList.get(position).getUuid());
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AccountActionsDialog dialog = new AccountActionsDialog(usersList.get(position).getProfilePicture(), usersList.get(position).getUserName()
                            , usersList.get(position).getIsActive(), callBack, usersList.get(position).getPassword(),usersList.get(position).getUserId(),
                            usersList.get(position).getUuid()
                            );
                    dialog.setCancelable(true);
                    dialog.show(childFragmentManager, "");
                }
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
