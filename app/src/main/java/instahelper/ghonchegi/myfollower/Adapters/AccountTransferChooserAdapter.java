package instahelper.ghonchegi.myfollower.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
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
import instahelper.ghonchegi.myfollower.Interface.AccountTransferInfoInterface;
import instahelper.ghonchegi.myfollower.Models.User;
import instahelper.ghonchegi.myfollower.R;

import static instahelper.ghonchegi.myfollower.App.TAG;

public class AccountTransferChooserAdapter extends RecyclerView.Adapter<AccountTransferChooserAdapter.Item> {

    private final Dialog dialog;
    AccountTransferInfoInterface callback;
    private ArrayList<User> usersList;
    private Context context;


    public AccountTransferChooserAdapter(ArrayList<User> usersList, AccountTransferInfoInterface callBack, Dialog dialogFragment) {
        this.usersList = usersList;
        this.callback = callBack;
        this.dialog = dialogFragment;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        this.context = parent.getContext();
        View row = LayoutInflater.from(context).inflate(R.layout.cell_account_management_v2, parent, false);
        return new AccountTransferChooserAdapter.Item(row);

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
            Log.i(TAG, "onBindViewHolder: " + usersList.get(position).getUuid());
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.sendUUID(usersList.get(position).getUuid(), usersList.get(position).getProfilePicture());
                    dialog.dismiss();
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
